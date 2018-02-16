package org.usfirst.frc.team4361.robot;
import MotorControllers.*;
import edu.wpi.first.wpilibj.Encoder;
import Util.*;

public class Elevator
{
	private Drive Elevator;
	private Encoder[] enc;
	private int Position = 0;
	
	private double elevatorSpeed;
	public Elevator(Drive Elevator)
	{
		this.Elevator = Elevator;
		
		Constants cons = Constant.AllConstant;
		
		elevatorSpeed = cons.GetDouble("elevatorSpeed");
	}
	public Elevator(Drive Elevator, Encoder[] enc)
	{
		this(Elevator);
		this.enc = enc;
	}
	
	public void Lower()
	{

		if(Position != 0)
			Position ++;
	}
	
	public void Raise()
	{
		if(Position != 3)
			Position ++;
	}
	
	public void Set(int pos)
	{
		if(pos >=0 && pos <=3)
			Position = pos;
	}
	
	public void ElevatorRun()
	{
		
	}
	
	public void Manual(double speed)
	{
		Elevator.drive(speed);
	}
	
}
