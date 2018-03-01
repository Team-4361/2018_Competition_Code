package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

import MotorControllers.*;
import Util.*;

public class Elevator
{
	public enum Position
	{
		Lower, LowMid, Middle, MidUp, Upper
	}
	
	private Drive Elevator;
	private DigitalInput[] lim;
	private DigitalInput StopLim, bottomSwitch;
	private Encoder enc;
	private Position Position, lastPosition;
	
	private Timer timer;
	
	private boolean Climb, HasRun;
	
	private double elevatorSpeed, elevatorDownSpeed, climbSpeed;
	private double SwitchHeight, StopHeight, ScaleHeight;
	private double ElevatorRatio, ElevatorDifference;
	
	private DoubleSolenoid StopSol;
	
	
	public Elevator(Drive Elevator)
	{
		this.Elevator = Elevator;
		
		Position = Position.Lower;
		lastPosition = Position.Lower;
		
		timer = new Timer();
		
		Climb = false;
		HasRun = false;
		
		Constants cons = Constant.AllConstant;
		
		elevatorSpeed = cons.GetDouble("elevatorSpeed");
		elevatorDownSpeed = cons.GetDouble("elevatorDownSpeed");
		climbSpeed = cons.GetDouble("climbSpeed");
		
		SwitchHeight = cons.GetDouble("SwitchHeight");
		StopHeight = cons.GetDouble("StopHeight");
		ScaleHeight = cons.GetDouble("ScaleHeight");
		
		ElevatorRatio = cons.GetDouble("ElevatorRatio");
		ElevatorDifference = cons.GetDouble("ElevatorDifference");
	}
	public Elevator(Drive Elevator, DoubleSolenoid StopSol)
	{
		//Manual
		this(Elevator);
		this.StopSol = StopSol;
	}
	public Elevator(Drive Elevator, DoubleSolenoid StopSol, DigitalInput StopLim, DigitalInput[] lim)
	{
		//Limit Switch based
		this(Elevator, StopSol);
		this.lim = lim;
		this.StopLim = StopLim;
	}
	public Elevator(Drive Elevator, DoubleSolenoid StopSol, DigitalInput bottomSwitch)
	{
		//Time based
		this(Elevator, StopSol);
		this.bottomSwitch = bottomSwitch;
	}
	public Elevator(Drive Elevator, DoubleSolenoid StopSol, DigitalInput bottomSwitch, Encoder enc)
	{
		//Encoder based
		this(Elevator, StopSol, bottomSwitch);
		this.enc = enc;
	}
	
	//Position changers
	public void Lower()
	{
		if(Position != Position.Lower && !HasRun)
			Position = ConvertNumToPosition(ConvertPositionToNum(Position)-2);
	}
	public void Raise()
	{
		if(Position != Position.Upper && !HasRun)
			Position = ConvertNumToPosition(ConvertPositionToNum(Position)+2);
	}
	public void Set(Position pos)
	{
		if(pos == Position.Lower || pos == Position.Middle || pos == Position.Upper && !HasRun)
			Position = pos;
	}
	
	//Actually movement
	public void ElevatorRun()
	{
		SensorRun();
		//TimeRun();
	}
	private void SensorRun()
	{
		if(lim == null || Climb)
			return;
		
		int Real = ConvertPositionToNum(GetRealPosition()), pos = ConvertPositionToNum(Position);
		
		if(Real != pos)
		{
			if(Real < pos)
				Elevator.drive(elevatorSpeed);
			else if(Real > pos)
				Elevator.drive(-elevatorDownSpeed);
			else
				Elevator.drive(0);
		}
		else
			Elevator.drive(0);
	}
	private void TimeRun()
	{
		if(Position == Position.Lower && bottomSwitch.get())
		{
			lastPosition = Position.Lower;
			Elevator.drive(elevatorDownSpeed);
		}
		else
		{
			
			boolean move = false;
			double time = 0;
			
			//Middle moves
			if(Position == Position.Middle)
			{
				if(lastPosition == Position.Lower)
				{
					move = true;
					time = 0;
				}
				if(lastPosition == Position.Upper)
				{
					move = true;
					time = 0;
				}
			}
			
			//Upper moves
			if(Position == Position.Upper)
			{
				if(lastPosition == Position.Lower)
				{
					move = true;
					time = 0;
				}
				if(lastPosition == Position.Middle)
				{
					move = true;
					time = 0;
				}
			}
			
			//Moves
			if(move)
			{
				if(!HasRun)
				{
					Elevator.drive(elevatorSpeed);
					timer.start();
					HasRun = true;
				}
				
				if(HasRun && timer.get() >= time)
				{
					Elevator.drive(0);
					timer.stop();
					timer.reset();
					HasRun = false;
					lastPosition = Position;
				}
			}
		}
	}
	
	//Different position things
	public Position GetRealPosition()
	{
		//return EncoderPosition();
		//return EasyLimPosition();
		return HardLimPosition();
	}
	private Position EncoderPosition()
	{
		if(bottomSwitch.get())
		{
			enc.reset();
			return Position.Lower;
		}
		else if(Math.abs(SwitchHeight - GetHeight()) < ElevatorDifference)
			return Position.Middle;
		else if(Math.abs(ScaleHeight - GetHeight()) < ElevatorDifference)
			return Position.Upper;
		else if(0 < GetHeight() && GetHeight() < SwitchHeight)
			return Position.LowMid;
		else if(SwitchHeight < GetHeight() && GetHeight() < ScaleHeight)
			return Position.MidUp;
		
		return Position.Lower;
	}
	public Position EasyLimPosition()
	{
		//Need to fix Middle position from going down
		if(lim[0].get())
		{
			return Position.Lower;
		}
		else if(lim[2].get())
		{
			return Position.Middle;
		}
		else if(lim[1].get())
		{
			return Position.Middle;
		}
		
		return Position.Lower;
	}
	public Position HardLimPosition()
	{
		//Case by case basis
		if(lim[0].get())
		{
			lastPosition = Position.Lower;
			return Position.Lower;
		}
		else if((lastPosition == Position.Lower && Elevator.GetSpeed() > 0) || (lastPosition == Position.Middle && Elevator.GetSpeed() < 0))
		{
			return Position.MidUp;
		}
		else if(lim[2].get())
		{
			lastPosition = Position.Middle;
			return Position.Middle;
		}
		else if((lastPosition == Position.Middle && Elevator.GetSpeed() > 0) || (lastPosition == Position.Middle && Elevator.GetSpeed() < 0))
		{
			return Position.LowMid;
		}
		else if(lim[1].get())
		{
			lastPosition = Position.Middle;
			return Position.Middle;
		}
		
		return Position.Lower;
	}
	
	public void Manual(double speed)
	{
		Elevator.drive(speed);
	}
	
	public void Climb()
	{
		if(StopLim.get() && !Climb)
		{
			Elevator.drive(-climbSpeed);
			Climb = true;
		}
		else if(Climb)
		{
			Elevator.drive(0);
			StopSol.set(DoubleSolenoid.Value.kForward);
		}
	}

	public int ConvertPositionToNum(Position pos)
	{
		if(pos == Position.Lower)
			return 0;
		else if(pos == Position.LowMid)
			return 1;
		else if(pos == Position.Middle)
			return 2;
		else if(pos == Position.MidUp)
			return 3;
		else if(pos == Position.Upper)
			return 4;
		
		return 0;
	}

	public Position ConvertNumToPosition(int num)
	{
		if(num == 0)
			return Position.Lower;
		else if(num == 1)
			return Position.LowMid;
		else if(num == 2)
			return Position.Middle;
		else if(num == 3)
			return Position.MidUp;
		else if(num == 4)
			return Position.Upper;
		
		return Position.Lower;
	}
	public double GetHeight()
	{
		return enc.get() * ElevatorRatio;
	}
	
}
