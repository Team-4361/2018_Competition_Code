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
	Intake intake;
	Elevator elevator;

	AutonomousMethods methods;
	
	public enum Position {Left, Middle, Right}
	public Position position = Position.Left;
	
	double StationWidth=72, ExchangerWidth=36, ExchangerDepth=48, SwitchDepth=180, SwitchWidth=36, WallToAutoLine=120, AutoLineToSwitch=48, SwitchToMidNull=120, SwitchToWall=72, ArcadeDepth=324, WallToScale=72, BoxWidth=13, PortalDepth, CubeZoneWidth;

	double RobotWidth=39.5, RobotDepth=35;
	
	public Autonomous(TankDrive chassis, Intake intake, Elevator elevator, Position pos, Encoder rEnc, Encoder lEnc)
	{
		RunNum = new Counter();
		cons = Constant.AllConstant;
		
		this.chassis = chassis;
		this.intake = intake;
		this.elevator = elevator;
		
		position = pos;
		
		double circumference = cons.GetDouble("WheelDiameter")*Math.PI;
		
		StationWidth = cons.GetDouble("StationWidth");
		ExchangerWidth = cons.GetDouble("ExchangerWidth");
		ExchangerDepth = cons.GetDouble("ExchangerDepth");
		SwitchDepth = cons.GetDouble("SwitchDepth");
		SwitchWidth = cons.GetDouble("SwitchWidth");
		WallToAutoLine = cons.GetDouble("WallToAutoLine");
		AutoLineToSwitch = cons.GetDouble("AutoLineToSwitch");
		SwitchToMidNull = cons.GetDouble("SwitchToMidNull");
		SwitchToWall = cons.GetDouble("SwitchToWall");
		ArcadeDepth = cons.GetDouble("ArcadeDepth");
		WallToScale = cons.GetDouble("WallToScale");
		BoxWidth = cons.GetDouble("BoxWidth");
		PortalDepth = cons.GetDouble("PortalDepth");
		CubeZoneWidth = cons.GetDouble("CubeZoneWidth");
		
		RobotWidth = cons.GetDouble("RobotWidth");
		RobotDepth = cons.GetDouble("RobotDepth");
		
		methods = new AutonomousMethods(RunNum, circumference, true, chassis, rEnc, lEnc);
	}
	
	public void DriveToLine()
	{
		if(RunNum.Get() == 0)
			methods.goDistance(0, .5);
	}
	
	public void Switch()
	{
		elevator.ElevatorRun();
		
		if(position == Position.Left)
		{
			if(RunNum.Get() == 0)
				methods.goDistance( -RobotDepth/2 + WallToAutoLine + AutoLineToSwitch + SwitchWidth + RobotWidth/2, .5);
			if(RunNum.Get() == 1)
				methods.turn(90, .5);
			if(RunNum.Get() == 2)
			{
				elevator.Set(1);
				RunNum.Add();
			}
			if(RunNum.Get() == 3)
				methods.goDistance(SwitchToWall - PortalDepth - RobotWidth/2 + RobotWidth*3/2, .5);
			if(RunNum.Get() == 4)
				methods.goDistance(-RobotWidth/2, .2);
			if(RunNum.Get() == 5)
				methods.turn(90, .5);
			if(RunNum.Get() == 6)
				methods.goDistance((RobotWidth-RobotDepth)/2, .2);
			if(RunNum.Get() == 7)
			{
				intake.outtake();
				methods.wait(.5);
			}
			if(RunNum.Get() == 8)
			{
				intake.stopIntake();
				methods.goDistance(-(RobotWidth-RobotDepth)/2, .5);
			}
				
		}
		else if(position == Position.Middle)
		{
			if(RunNum.Get() == 0)
				methods.goDistance(ExchangerWidth+(WallToAutoLine+AutoLineToSwitch-ExchangerWidth-CubeZoneWidth)/2, .2);
			if(RunNum.Get() == 1)
				methods.turn(-90, .5);
			if(RunNum.Get() == 2)
				methods.goDistance(PortalDepth+ExchangerDepth+StationWidth-SwitchToWall-RobotWidth/2, .2);
			if(RunNum.Get() == 3)
			{
				elevator.Set(1);
				RunNum.Add();
			}
			if(RunNum.Get() == 4)
				methods.turn(90, .5);
			if(RunNum.Get() == 5)
				methods.goDistance(WallToAutoLine+AutoLineToSwitch-(ExchangerWidth+(WallToAutoLine+AutoLineToSwitch-ExchangerWidth-CubeZoneWidth)/2)-RobotDepth/2, .2);
			if(RunNum.Get() == 6)
			{
				intake.outtake();
				methods.wait(.5);
			}
			if(RunNum.Get() == 7)
			{
				intake.stopIntake();
				RunNum.Add();
			}
		}
		else if(position == Position.Right)
		{
			if(RunNum.Get() == 0)
				methods.goDistance(WallToAutoLine+AutoLineToSwitch+SwitchWidth+BoxWidth+3, .5);
			if(RunNum.Get() == 1)
				methods.turn(-90, .5);
			if(RunNum.Get()==2)
				methods.goDistance(ArcadeDepth/2-PortalDepth-RobotWidth/2+SwitchDepth/2-RobotWidth/2, .5);
			if(RunNum.Get()==3)
			{
				elevator.Set(1);
				RunNum.Add();
			}
			if(RunNum.Get()==4)
				methods.turn(-90, .5);
			//if(RunNum.Get() == 5)
					
   		}
	}
	
	public void Scale()
	{
		if(position == Position.Left)
		{
			
		}
		else if(position == Position.Middle)
		{
			
		}
		else if(position == Position.Right)
		{
			
		}
	}
	
	public void Switch2()
	{
		if(position == Position.Left)
		{
			
		}
		else if(position == Position.Middle)
		{
			
		}
		else if(position == Position.Right)
		{
			
		}
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
