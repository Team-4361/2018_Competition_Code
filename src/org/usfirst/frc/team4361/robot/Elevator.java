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
		
		Constants cons = new Constants();
		cons.LoadConstants();
		
		elevatorSpeed = cons.GetDouble("elevatorSpeed");
	}
	public Elevator(Drive Elevator, Encoder[] enc)
	{
		this(Elevator);
		this.enc = enc;
	}
	
	public void Lower()
	{
		
	}
	
	public void Raise()
	{
		
	}
	
	public void Set(int pos)
	{
		
	}
	
	public void Manual(double speed)
	{
		Elevator.drive(speed);
	}
	
}
