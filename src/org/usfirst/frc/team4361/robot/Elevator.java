package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

import Controllers.*;
import Util.*;

public class Elevator
{
	public enum Position
	{
		Lower, LowMid, Middle, MidUp, Stop, Upper
	}
	
	private Drive Elevator;
	private DigitalInput[] lim;
	private DigitalInput bottomSwitch;
	private Encoder enc;
	private Position position, lastPosition;
	
	private Timer timer;
	
	private boolean Climb, HasRun;
	
	private double elevatorSpeed, elevatorDownSpeed, climbSpeed;
	private double SwitchHeight, StopHeight, ScaleHeight;
	private double ElevatorRatio, ElevatorDifference;
	
	
	
	public Elevator(Drive Elevator)
	{
		this.Elevator = Elevator;
		
		position = Position.Lower;
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
	public Elevator(Drive Elevator, DigitalInput[] lim)
	{
		//Limit Switch based
		this(Elevator);
		this.lim = lim;
	}
	public Elevator(Drive Elevator, DigitalInput bottomSwitch)
	{
		//Time based
		this(Elevator);
		this.bottomSwitch = bottomSwitch;
	}
	public Elevator(Drive Elevator, DigitalInput bottomSwitch, Encoder enc)
	{
		//Encoder based
		this(Elevator, bottomSwitch);
		this.enc = enc;
	}
	
	//Position changers
	public void Lower()
	{
		if(position != Position.Lower && !HasRun)
			position = ConvertNumToPosition(ConvertPositionToNum(position)-2);
	}
	public void Raise()
	{
		if(position != Position.Upper && !HasRun)
			position = ConvertNumToPosition(ConvertPositionToNum(position)+2);
	}
	public void Set(Position pos)
	{
		if(pos == Position.Lower || pos == Position.Middle || pos == Position.Upper && !HasRun)
			position = pos;
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
		
		int Real = ConvertPositionToNum(GetRealPosition()), pos = ConvertPositionToNum(position);
		
		if(Real == -1)
			return;
		
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
		if(position == Position.Lower && bottomSwitch.get())
		{
			lastPosition = Position.Lower;
			Elevator.drive(elevatorDownSpeed);
		}
		else
		{
			
			boolean move = false;
			double time = 0;
			
			//Middle moves
			if(position == Position.Middle)
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
			if(position == Position.Upper)
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
					lastPosition = position;
				}
			}
		}
	}
	
	public Position GetSetPosition()
	{
		return position;
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
			return Position.Upper;
		}
		else if(lim[1].get())
		{
			return Position.Middle;
		}
		
		return null;
	}
	public Position HardLimPosition()
	{
		//Case by case basis
		if(lim[0].get())
		{
			lastPosition = Position.Lower;
			return Position.Lower;
		}
		else if(lim[1].get())
		{
			lastPosition = Position.Middle;
			return Position.Middle;
		}
		else if(lim[2].get())
		{
			lastPosition = Position.Upper;
			return Position.Upper;
		}
		else if(lim[3].get())
		{
			lastPosition = Position.MidUp;
			return Position.MidUp;
		}
		else if((lastPosition == Position.Upper && Elevator.GetSpeed() > 0) || (lastPosition == Position.Middle && Elevator.GetSpeed() < 0) || lastPosition == Position.MidUp)
		{
			lastPosition = Position.MidUp;
			return Position.MidUp;
		}
		else if((lastPosition == Position.Lower && Elevator.GetSpeed() < 0) || (lastPosition == Position.Middle && Elevator.GetSpeed() > 0) || lastPosition == Position.LowMid)
		{
			lastPosition = Position.LowMid;
			return Position.LowMid;
		}
		
		if(position == Position.Lower || position == Position.Upper)
			return Position.Middle;
		else if(lastPosition == Position.Middle)
		{
			lastPosition = Position.LowMid;
			return Position.LowMid;
		}
		
		return null;
	}
	
	public void Manual(double speed)
	{
		if(speed > 0 && GetRealPosition() == Position.Lower || speed < 0 && GetRealPosition() == Position.Upper)
			Elevator.drive(0);
		else
			Elevator.drive(speed);
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
		
		return -1;
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
		
		return null;
	}
	public double GetHeight()
	{
		return enc.get() * ElevatorRatio;
	}
	
	public Drive GetMotor()
	{
		return Elevator;
	}
	
}
