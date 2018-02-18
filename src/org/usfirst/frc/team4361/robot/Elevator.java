package org.usfirst.frc.team4361.robot;
import MotorControllers.*;
import edu.wpi.first.wpilibj.DigitalInput;
import Util.*;

public class Elevator
{
	private Drive Elevator;
	private DigitalInput[] lim;
	private int Position = 0;
	
	private double elevatorSpeed;
	public Elevator(Drive Elevator)
	{
		this.Elevator = Elevator;
		
		Constants cons = Constant.AllConstant;
		
		elevatorSpeed = cons.GetDouble("elevatorSpeed");
	}
	public Elevator(Drive Elevator, DigitalInput[] lim)
	{
		this(Elevator);
		this.lim = lim;
	}
	
	public void Lower()
	{
		if(Position != 0)
			Position ++;
	}
	
	public void Raise()
	{
		if(Position != 2)
			Position ++;
	}
	
	public void Set(int pos)
	{
		if(pos >=0 && pos <=2)
			Position = pos;
	}
	
	public void ElevatorRun()
	{
		if(GetRealPosition() != Position)
		{
			if(GetRealPosition() < Position)
				Elevator.drive(elevatorSpeed);
			else if(GetRealPosition() < Position)
				Elevator.drive(-elevatorSpeed);
			else
				Elevator.drive(0);
		}
		else
			Elevator.drive(0);
	}
	
	public int GetRealPosition()
	{
		//Case by case basis
		if(lim[0].get())
			return 0;
		else if(lim[2].get())
			return 2;
		else if(lim[1].get())
			return 1;
		
		return 0;
	}
	
	public void Manual(double speed)
	{
		Elevator.drive(speed);
	}
	
}
