package org.usfirst.frc.team4361.robot;
import MotorControllers.*;
import edu.wpi.first.wpilibj.Encoder;
import Util.*;

public class Elevator
{
	private Drive Elevator, lInt, rInt;
	private Encoder[] enc;
	private int Position = 0;
	
	private double intakeSpeed, outtakeSpeed, elevatorSpeed;
	public Elevator(Drive Elevator, Drive lInt, Drive rInt)
	{
		this.Elevator = Elevator;
		this.lInt = lInt;
		this.rInt = rInt;
		
		Constants cons = new Constants();
		cons.LoadConstants();
		
		intakeSpeed = cons.GetDouble("intakeSpeed");
		outtakeSpeed = cons.GetDouble("outtakeSpeed");
		elevatorSpeed = cons.GetDouble("elevatorSpeed");
	}
	public Elevator(Drive Elevator, Drive lInt, Drive rInt, Encoder[] enc)
	{
		this(Elevator, lInt, rInt);
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
	
	public void Intake()
	{
		lInt.drive(intakeSpeed);
		rInt.drive(intakeSpeed);
	}
	
	public void Outtake()
	{
		lInt.drive(outtakeSpeed);
		rInt.drive(outtakeSpeed);
	}
	
	public void StopIntake()
	{
		lInt.drive(0);
		rInt.drive(0);
	}
	
}
