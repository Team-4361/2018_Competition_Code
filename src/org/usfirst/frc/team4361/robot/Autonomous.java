package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid;

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

	public AutonomousMethods methods;

	String FMS;
	boolean hasCube = false, hasRun = false;
	Timer timer;
	
	DoubleSolenoid pushSol;
	
	double StationWidth=72, ExchangerWidth=36, ExchangerDepth=48, SwitchDepth=180, SwitchWidth=36, WallToAutoLine=120, AutoLineToSwitch=48, SwitchToMidNull=120, SwitchToWall=72, ArcadeDepth=324, WallToScale=72, BoxWidth=13, PortalDepth, CubeZoneWidth, ScalePlate;

	double RobotWidth=39.5, RobotDepth=35;
	
	public Autonomous(TankDrive chassis, Intake intake, Elevator elevator, String FMS, Encoder rEnc, Encoder lEnc, DoubleSolenoid pushSol)
	{
		RunNum = new Counter();
		cons = Constant.AllConstant;
		
		this.chassis = chassis;
		this.intake = intake;
		this.elevator = elevator;
		
		hasRun = false;
		timer = new Timer();
		
		this.pushSol = pushSol;
		
		this.FMS = FMS;
		
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
		ScalePlate = cons.GetDouble("ScalePlate");
		
		RobotWidth = cons.GetDouble("RobotWidth");
		RobotDepth = cons.GetDouble("RobotDepth");
		
		methods = new AutonomousMethods(RunNum, circumference, true, chassis, lEnc, rEnc);
	}
	
	public void DriveToLine()
	{
		if(RunNum.Get() == 0)
		{
			pushSol.set(DoubleSolenoid.Value.kForward);
			methods.goDistance(WallToAutoLine, .5);
		}
		else if(RunNum.Get() == 1)
		{
			pushSol.set(DoubleSolenoid.Value.kReverse);
			RunNum.Add();
		}
			
	}
	
	public void Side(char side)
	{
		int angleRev = 1;
		if(side == 'L')
			angleRev = -1;

		if(FMS == "" || (FMS.charAt(0) != side && FMS.charAt(1) != side))
		{
			DriveToLine();
		}
		else if(hasCube)
		{
			//Start with cube
			if(RunNum.Get() == 0)
			{
				pushSol.set(DoubleSolenoid.Value.kForward);
				methods.wait(.2);
			}
			else if(RunNum.Get() == 1)
			{
				pushSol.set(DoubleSolenoid.Value.kReverse);
				RunNum.Add();
			}

			//Scale on Side
			else if(FMS != "" && FMS.charAt(1) == side)
			{
				if(RunNum.Get() == 2)
					methods.goDistance(WallToAutoLine + AutoLineToSwitch + SwitchWidth + SwitchToMidNull, 1);
				else if(RunNum.Get() == 3)
					methods.goDistance(RobotWidth*2, .6);
				else if(RunNum.Get() == 4)
					methods.turn(-90*angleRev, .7);
				else if(RunNum.Get() == 5)
				{
					elevator.Set(Elevator.Position.Upper);
					methods.goDistance(PortalDepth - (RobotWidth-RobotDepth)/2, -.3);
				}
				else if(RunNum.Get() == 6)
				{
					methods.wait(1.4);
				}
				else if(RunNum.Get() == 7)
					methods.goDistance(WallToScale/2, .2);
				else if(RunNum.Get() == 8)
				{
					intake.openIntake();
					RunNum.Add();
				}
				else if(RunNum.Get() == 9)
					methods.wait(.4);
				else if(RunNum.Get() == 10)
				{
					methods.goDistance(WallToScale/8, -.3);
				}
				else if(RunNum.Get() == 11)
				{
					elevator.Set(Elevator.Position.Lower);
					methods.goDistance(WallToScale/8, -.3);
				}

				else if(FMS.charAt(0) == side && true)
				{
					double distanceWidth = SwitchToMidNull + RobotWidth*2, distanceDepth = SwitchToWall - WallToScale/4;
					
					//Might go for a switch cube too
					if(RunNum.Get() == 12)
						methods.turn(-68*angleRev, .6);
					else if(RunNum.Get() == 13)
						methods.goDistance(126, .7);
					else if(RunNum.Get() == 14)
					{
						intake.closeIntake();
						intake(.4);
					}
					else if(RunNum.Get() == 15)
					{
						elevator.Set(Elevator.Position.Middle);
						methods.wait(.7);
					}
					else if(RunNum.Get() == 16)
						methods.goDistance(10, .5);
					else if(RunNum.Get() == 17)
					{
						intake.openIntake();
						RunNum.Add();
					}
				}
			}
			//Switch on side
			else if(FMS != "" && FMS.charAt(0) == side)
			{
				if(RunNum.Get() == 2)
				{
					elevator.Set(Elevator.Position.Middle);
					methods.goDistance(WallToAutoLine + AutoLineToSwitch + SwitchWidth/2 + RobotWidth/2, .5);
				}
				else if(RunNum.Get() == 3)
					methods.turn(-90*angleRev, .5);
				else if(RunNum.Get() == 4)
					methods.goDistance(SwitchToWall - PortalDepth - RobotWidth + 10, .3);
				else if(RunNum.Get() == 5)
					outtake(.7);
			}
		}
		else
		{
			//Get cube in auto
			if(RunNum.Get() == 0)
			{
				pushSol.set(DoubleSolenoid.Value.kForward);
				methods.goDistance(WallToAutoLine + AutoLineToSwitch + SwitchWidth + BoxWidth + (RobotWidth-RobotDepth)/2 + 5, .5);
			}
			if(RunNum.Get() == 1)
			{
				pushSol.set(DoubleSolenoid.Value.kReverse);
				methods.turn(-90 * angleRev, .7);
			}
			if(RunNum.Get() == 2)
				methods.goDistance(SwitchToWall - PortalDepth - RobotDepth/2 + BoxWidth/2, .2);
			if(RunNum.Get() == 3)
			{
				elevator.Set(Elevator.Position.Middle);
				intake.openIntake();
				methods.wait(.5);
			}
			if(RunNum.Get() == 4)
				methods.turn(-90 * angleRev, .7);
			if(RunNum.Get() == 5)
			{
				elevator.Set(Elevator.Position.Lower);
				methods.wait(.5);
			}
			if(RunNum.Get() == 6)
			{
				methods.goDistance(5, .2);
				intake.intake();
			}
			if(RunNum.Get() == 7)
			{
				intake.closeIntake();
				intake.intake();
				methods.wait(.3);
			}
			if(RunNum.Get() == 8)
			{
				intake.stopIntake();
				RunNum.Add();
			}
			
			
			//Scale
			if(FMS.charAt(1) == side)
			{
				if(RunNum.Get() == 9)
					methods.turn(90*angleRev, .6);
				if(RunNum.Get() == 10)
					methods.goDistance(BoxWidth, -.2);
				if(RunNum.Get() == 11)
					methods.turn(90*angleRev, .5);
				if(RunNum.Get() == 12)
				{
					elevator.Set(Elevator.Position.Upper);
					methods.goDistance(SwitchToMidNull-BoxWidth-ScalePlate/2-RobotWidth/2, .4);
				}
				if(RunNum.Get() == 13)
					outtake(.3);
			}
			//Switch
			else if(FMS.charAt(0) == side)
			{
				if(RunNum.Get() == 9)
				{
					methods.goDistance(BoxWidth, .2);
				}
				if(RunNum.Get() == 10)
					outtake(.5);
			}
		}
	}
	
	public void SimpleSwitch(char side)
	{
		if(FMS != "" && FMS.charAt(0) == side)
		{
			if(RunNum.Get() == 0)
			{
				pushSol.set(DoubleSolenoid.Value.kForward);
				methods.wait(1.0);
			}
			else if(RunNum.Get() == 1)
			{
				intake.openIntake();
				intake.intake();
				methods.wait(1.0);
			}
			else if(RunNum.Get() == 2)
			{
				intake.closeIntake();
				methods.wait(1.0);
			}
			else if(RunNum.Get() == 3)
			{
				intake.stopIntake();
				pushSol.set(DoubleSolenoid.Value.kReverse);
				elevator.Set(Elevator.Position.Middle);
				methods.wait(1.0);
			}
			else if(RunNum.Get() == 4)
			{
				methods.goDistance(WallToAutoLine + AutoLineToSwitch - RobotWidth + 5, .5);
			}
			else if(RunNum.Get() == 5)
			{
				intake.openIntake();
				RunNum.Add();
			}
		}
		else
		{
			DriveToLine();
		}
	}
	
	public void Middle()
	{
		int angleRev = 1;
		if(FMS.charAt(0) == 'L')
			angleRev = -1;
		
		if(hasCube)
		{
			if(RunNum.Get() == 0)
			{
				pushSol.set(DoubleSolenoid.Value.kForward);
				methods.wait(.2);
			}
			else if(RunNum.Get() == 1)
			{
				pushSol.set(DoubleSolenoid.Value.kReverse);
				methods.goDistance(ExchangerWidth + RobotDepth - 5, .6);
			}
			else if(RunNum.Get() == 2)
			{
				elevator.Set(Elevator.Position.Middle);
				methods.turn(90*angleRev, .6);
			}
			else if(RunNum.Get() == 3)
				methods.goDistance(ArcadeDepth/2 - SwitchToWall - RobotDepth/2 - (StationWidth-ExchangerDepth)/2*angleRev, .5);
			else if(RunNum.Get() == 4)
				methods.turn(-90*angleRev, .6);
			else if(RunNum.Get() == 5)
				methods.goDistance(WallToAutoLine + AutoLineToSwitch - ExchangerWidth - RobotDepth + 5, .5);
			else if(RunNum.Get() == 6)
			{
				intake.openIntake();
				RunNum.Add();
			}
		}
		else
		{
			if(RunNum.Get() == 0)
			{
				pushSol.set(DoubleSolenoid.Value.kForward);
				methods.goDistance(ExchangerWidth + RobotWidth, .3);
			}
			else if(RunNum.Get() == 1)
			{
				pushSol.set(DoubleSolenoid.Value.kReverse);
				methods.turn(-90, .7);
			}
			else if(RunNum.Get() == 2)
				methods.goDistance((StationWidth-ExchangerDepth)/2, .2);
			else if(RunNum.Get() == 3)
				methods.turn(90, .7);
			else if(RunNum.Get() == 4)
			{
				intake.openIntake();
				methods.goDistance(WallToAutoLine + AutoLineToSwitch - CubeZoneWidth - RobotWidth - ExchangerWidth - RobotWidth, .4);
			}
			else if(RunNum.Get() == 5)
			{
				intake.intake();
				intake.closeIntake();
				methods.wait(0.3);
			}
			else if(RunNum.Get() == 6)
			{
				elevator.Set(Elevator.Position.Middle);
				methods.turn(90*angleRev, .7);
			}
			else if(RunNum.Get() == 7)
				methods.goDistance(ArcadeDepth/2 - SwitchToWall - RobotDepth/2, .4);
			else if(RunNum.Get() == 8)
				methods.turn(-90*angleRev, .7);
			else if(RunNum.Get() == 9)
				methods.goDistance(CubeZoneWidth + RobotWidth/2, .4);
			else if(RunNum.Get() == 10)
				outtake(.5);
		}
			
	}
	
	public void MidScale()
	{
		int angleRev = 1;
		if(FMS.charAt(1) == 'L')
			angleRev = -1;
		
		if(hasCube)
		{
			if(RunNum.Get() == 0)
			{
				pushSol.set(DoubleSolenoid.Value.kForward);
				methods.wait(.5);
			}
			else if(RunNum.Get() == 1)
			{
				pushSol.set(DoubleSolenoid.Value.kReverse);
				methods.goDistance(ExchangerWidth + RobotWidth, .3);
			}
			else if(RunNum.Get() == 2)
			{
				elevator.Set(Elevator.Position.Middle);
				methods.turn(90*angleRev, .7);
			}
			else if(RunNum.Get() == 3)
				methods.goDistance(ArcadeDepth/2 - RobotWidth/2 - (StationWidth-ExchangerDepth)/2*angleRev, .4);
			else if(RunNum.Get() == 4)
				methods.turn(-90*angleRev, .7);
			else if(RunNum.Get() == 5)
				methods.goDistance(WallToAutoLine + AutoLineToSwitch + SwitchWidth + SwitchToMidNull - ExchangerWidth - RobotWidth, .6);
			else if(RunNum.Get() == 6)
				methods.turn(-90*angleRev, .7);
			else if(RunNum.Get() == 7)
			{
				elevator.Set(Elevator.Position.Upper);
				methods.wait(1.5);
			}
			else if(RunNum.Get() == 8)
				methods.goDistance(WallToScale - RobotWidth, .3);
			else if(RunNum.Get() == 9)
			{
				intake.openIntake();
				RunNum.Add();
			}
		}
		else if(false)
		{
			if(RunNum.Get() == 0)
			{
				pushSol.set(DoubleSolenoid.Value.kForward);
				methods.goDistance(ExchangerWidth + RobotWidth, .3);
			}
			else if(RunNum.Get() == 1)
			{
				pushSol.set(DoubleSolenoid.Value.kReverse);
				methods.turn(-90, .7);
			}
			else if(RunNum.Get() == 2)
				methods.goDistance((StationWidth-ExchangerDepth)/2, .2);
			else if(RunNum.Get() == 3)
				methods.turn(90, .7);
			else if(RunNum.Get() == 4)
			{
				intake.openIntake();
				methods.goDistance(WallToAutoLine + AutoLineToSwitch - CubeZoneWidth - RobotWidth - ExchangerWidth - RobotWidth, .4);
			}
			else if(RunNum.Get() == 5)
			{
				intake.intake();
				intake.closeIntake();
				methods.wait(0.3);
			}
			else if(RunNum.Get() == 6)
			{
				elevator.Set(Elevator.Position.Middle);
				methods.turn(90*angleRev, .7);
			}
			else if(RunNum.Get() == 7)
				methods.goDistance(ArcadeDepth/2 - SwitchToWall - RobotDepth/2, .4);
			else if(RunNum.Get() == 8)
				methods.turn(-90*angleRev, .7);
			else if(RunNum.Get() == 9)
				methods.goDistance(CubeZoneWidth + RobotWidth/2, .4);
			else if(RunNum.Get() == 10)
				outtake(.5);
		}
	}
	
	public void Dance()
	{
		if(RunNum.Get() == 0)
			methods.turn(90, .6);
		else if(RunNum.Get() == 2)
			methods.turn(-30, .6);
		else if(RunNum.Get() == 2)
			methods.turn(20, .6);
		else if(RunNum.Get() == 3)
			methods.goDistance(10, 1);
		else if(RunNum.Get() == 4)
			methods.turn(10, -1);
	}
	
	private void intake(double time)
	{
		if(!hasRun)
		{
			intake.intake();
			timer.reset();
			timer.start();
			hasRun = true;
		}
		else if(timer.get() >= time)
		{
			intake.stopIntake();
			timer.stop();
			timer.reset();
			hasRun = false;
			RunNum.Add();
		}
	}
	
	private void outtake(double time)
	{
		if(!hasRun)
		{
			intake.outtake();
			timer.reset();
			timer.start();
			hasRun = true;
		}
		else if(timer.get() >= time)
		{
			intake.stopIntake();
			timer.stop();
			timer.reset();
			hasRun = false;
			RunNum.Add();
		}
		
		
	}
}
