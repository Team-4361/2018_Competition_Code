package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.Encoder;

import Movement.AutonomousMethods;
import MotorControllers.Drive;
import Chassis.TankDrive;
import Util.*;


public class Autonomous
{
	Counter RunNum;
	Constants cons;
	
	TankDrive chassis;

	AutonomousMethods methods;
	
	public Autonomous(TankDrive chassis, boolean RedSide, Encoder rEnc, Encoder lEnc)
	{
		RunNum = new Counter();
		cons = Constant.GetConstants();
		
		this.chassis = chassis;
		
		double circumference = cons.GetDouble("");
		
		methods = new AutonomousMethods(RunNum, circumference, true, chassis, rEnc, lEnc);
	}
	
	public void DriveToLine()
	{
		if(RunNum.Get() == 0)
			methods.goDistance(0, .5);
	}
	
	public void Dance()
	{
		if(RunNum.Get() == 0)
			methods.turn(50, .6);
		if(RunNum.Get() == 1)
			methods.turn(-30, .6);
		if(RunNum.Get() == 2)
			methods.turn(20, .6);
		if(RunNum.Get() == 3)
			methods.goDistance(10, 1);
		if(RunNum.Get() == 4)
			methods.turn(10, -1);
	}
}
