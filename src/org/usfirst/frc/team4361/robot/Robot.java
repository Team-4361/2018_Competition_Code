package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.CameraServer;
import Chassis.*;
import Controls.*;
import Controllers.*;
import Util.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot
{
	String AutoSelected;

	Constants cons;
	
	JoystickTank Stick;
	XboxArcade Xbox;
	
	WPI_TalonSRX[] CAN;
	Drive left, right, ElevatorDrive, Climber;
	Drive lIntake, rIntake;
	
	DoubleSolenoid intSol, pushSol;
	
	Encoder lEnc, rEnc;
	DigitalInput Upper, Middle, Lower, MidUp;
	
	TankDrive chassis;
	Elevator elevator;
	Intake intake;
	Autonomous auto;
	PneumaticsControl Pneum;
	
	Change elevatorPress, intakePress, fixPress, gearPress;
	
	int Gear = 4;
	
	boolean XboxMode, demoMode, RedSide, Rumble, ManualElevator;
	
	String FMSdata;
	
	SendableChooser<String> chooser = new SendableChooser<>();
	
	
	@Override
	public void robotInit()
	{
		cons = Constant.GetConstants();
		Constant.AllConstant = cons;
		
		//Physical Peripherals
		Stick = new JoystickTank(cons.GetInt("LeftStick"), cons.GetInt("RightStick"));
		Xbox = new XboxArcade(cons.GetInt("Xbox"), Hand.kLeft);
		
		MotorSetup();
		
		intSol = new DoubleSolenoid(cons.GetInt("intFSol"), cons.GetInt("intRSol"));
		pushSol = new DoubleSolenoid(cons.GetInt("stopF"), cons.GetInt("stopR"));
		pushSol.set(DoubleSolenoid.Value.kReverse);
		
		lEnc = new Encoder(cons.GetInt("lEnc1"), cons.GetInt("lEnc2"));
		rEnc = new Encoder(cons.GetInt("rEnc1"), cons.GetInt("rEnc2"));
		
		Upper = new DigitalInput(cons.GetInt("Upper"));
		Middle = new DigitalInput(cons.GetInt("Middle"));
		Lower = new DigitalInput(cons.GetInt("Lower"));
		MidUp = new DigitalInput(cons.GetInt("Stop"));
		
		
		DigitalInput[] arr = {Lower, Middle, Upper, MidUp};
		
		chassis = new TankDrive(left, right, lEnc, rEnc);
		elevator = new Elevator(ElevatorDrive, arr);
		intake = new Intake(lIntake, rIntake, intSol);
		intake.closeIntake();
		
		Pneum = new PneumaticsControl(0, cons.GetInt("PressureSensor"), 4.547368);
		Pneum.GetCompressor().setClosedLoopControl(true);
		
		elevatorPress = new Change();
		intakePress = new Change();
		fixPress = new Change();
		gearPress = new Change();
		
		//Internal Variables
		XboxMode = false;
		demoMode = false;
		Rumble = true;
		ManualElevator = false;
		
		FMSdata = "";
		
		CameraSetup();
		
		chooser.addDefault("Drive to Line", "line");
		chooser.addObject("Left", "left");
		chooser.addObject("Middle", "mid");
		chooser.addObject("Right", "right");
		//chooser.addObject("Left Switch", "lswitch");
		//chooser.addObject("Right Switch", "rswitch");
		//chooser.addObject("Left Scale", "lscale");
		//chooser.addObject("Right Scale", "rscale");
		//chooser.addObject("Left Only", "lonly");
		//chooser.addObject("Right Only", "ronly");
		chooser.addObject("Dance", "dance");
		
		SmartDashboard.putData("Auto choices", chooser);
		SmartDashboard.putBoolean("XboxMode", XboxMode);
		SmartDashboard.putBoolean("Demonstration", demoMode);
		SmartDashboard.putBoolean("Cube", true);

		SmartDashboard.putBoolean("PrioritySwitch", false);
		SmartDashboard.putBoolean("Scale", true);
		SmartDashboard.putBoolean("Switch", true);
		SmartDashboard.putBoolean("Crossover", true);
		SmartDashboard.putBoolean("Second", true);
	}

	
	@Override
	public void autonomousInit() 
	{
		AutoSelected = chooser.getSelected();
		
		boolean hasCube = SmartDashboard.getBoolean("Cube", false);
		
		elevator.Set(Elevator.Position.Lower);
		
		auto = new Autonomous(chassis, intake, elevator, FMSdata, rEnc, lEnc, pushSol);
		auto.hasCube = hasCube;
	}

	@Override
	public void autonomousPeriodic()
	{
		elevator.ElevatorRun();
		
		switch(AutoSelected) {
		
		case "dance":
			auto.Dance();
			break;
			
		case "left":
			auto.Side('L', SmartDashboard.getBoolean("PrioritySwitch", true), SmartDashboard.getBoolean("Scale", true), SmartDashboard.getBoolean("Switch", true), SmartDashboard.getBoolean("Crossover", true), SmartDashboard.getBoolean("Second", true));
			break;
				
		case "mid":
			auto.Middle();
			break;
			
		case "right":
			auto.Side('R', SmartDashboard.getBoolean("PrioritySwitch", true), SmartDashboard.getBoolean("Scale", true), SmartDashboard.getBoolean("Switch", true), SmartDashboard.getBoolean("Crossover", true), SmartDashboard.getBoolean("Second", true));
			break;

		case "lswitch":
			auto.SimpleSwitch('L');
			break;
			
		case "rswitch":
			auto.SimpleSwitch('R');
			break;

		case "lscale":
			auto.SideScaleOnly('L');
			break;
			
		case "ronly":
			auto.SideOnly('R');
			break;
			
		case "lonly":
			auto.SideOnly('L');
			break;
			
		case "rscale":
			auto.SideScaleOnly('R');
			break;
			
		case "line":
		default:
			auto.DriveToLine();
			break;
		}
	}
	
	@Override
	public void teleopInit()
	{
		
	}
	
	@Override
	public void teleopPeriodic()
	{
		if(!ManualElevator)
			elevator.ElevatorRun();
		
		//Get values
		XboxMode = SmartDashboard.getBoolean("XboxMode", false);
		demoMode = SmartDashboard.getBoolean("Demonstration",  false);

		double[] DriveVal;
		
		//Controls
		if(XboxMode)
		{
			DriveVal = Xbox.GetDrive();
			
			if(Stick.right.getRawButton(4))
				elevator.Manual(Stick.right.getY());
			
			if(elevatorPress.State(Stick.right.getPOV() != -1))
			{
				if(Stick.right.getPOV() == 0)
					elevator.Raise();
				else if(Stick.right.getPOV() == 180)
					elevator.Lower();
			}
			
			//Elevator
			if(Stick.right.getRawButton(3))
				intake.intake();
			else if(Stick.right.getRawButton(4))
				intake.outtake();
			else if(Stick.right.getRawButton(6))
				intake.fastOuttake();
			else
				intake.stopIntake();

			intake.CubeFix(Stick.right.getRawButtonPressed(5));
			
			if(Stick.right.getRawButton(1))
				intake.SwitchIntake();
			

			if(gearPress.State(Stick.left.getPOV() != -1))
			{
				if(Stick.left.getPOV() == 0 && Gear < 4)
					Gear++;
				else if(Stick.left.getPOV() == 180 && Gear > 2)
					Gear--;
			}
		}
		else if(demoMode)
		{
			DriveVal = Xbox.GetDrive();
			
			//Elevator
			if(ManualElevator)
				elevator.Manual(Xbox.getY(Hand.kRight));
			
			if(elevatorPress.State(Math.abs(Xbox.getY(Hand.kRight)) < .1))
			{
				if(Xbox.getY(Hand.kRight) < 0)
					elevator.Raise();
				else if(Xbox.getY(Hand.kRight) > 0)
					elevator.Lower();
			}

			if(Xbox.getXButtonPressed())
				ManualElevator = !ManualElevator;
			
			//Intake
			if(Xbox.getAButton())
				intake.intake();
			else if(Xbox.getBButton())
				intake.outtake();
			else if(Xbox.getYButton())
				intake.fastOuttake();
			else
				intake.stopIntake();
			
			intake.CubeFix(Xbox.getBumperPressed(Hand.kRight));
			
			if(Xbox.getBumperPressed(Hand.kLeft))
				intake.SwitchIntake();
			

			if(gearPress.State(Xbox.getTriggerAxis(Hand.kRight) != 0 && Xbox.getTriggerAxis(Hand.kLeft) != 0))
			{
				if(Xbox.getTriggerAxis(Hand.kRight) > 0 && Gear < 4)
					Gear++;
				else if(Xbox.getTriggerAxis(Hand.kLeft) > 0 && Gear > 2)
					Gear--;
			}
		}
		else
		{
			if(Stick.right.getRawButton(4))
				DriveVal = Stick.GetPerfectStraight();
			else if(Stick.right.getRawButton(3))
				DriveVal = Stick.GetPerfectTurn();
			else
				DriveVal = Stick.GetDrive();
			
			//Elevator
			if(ManualElevator)
				elevator.Manual(Xbox.getY(Hand.kRight));
			
			if(elevatorPress.State(Xbox.getPOV() != -1))
			{
				if(Xbox.getPOV() == 0)
					elevator.Raise();
				else if(Xbox.getPOV() == 180)
					elevator.Lower();
			}
			
			if(Xbox.getXButtonPressed())
				ManualElevator = !ManualElevator;
			
			//Intake
			if(Xbox.getAButton())
				intake.intake();
			else if(Xbox.getBButton())
				intake.outtake();
			else if(Xbox.getYButton())
				intake.fastOuttake();
			else
				intake.stopIntake();
			
			intake.CubeFix(Xbox.getBumperPressed(Hand.kRight) || fixPress.State(Xbox.getTriggerAxis(Hand.kRight) != 0));
			
			if(Xbox.getBumperPressed(Hand.kLeft) || intakePress.State(Xbox.getTriggerAxis(Hand.kLeft) != 0))
				intake.SwitchIntake();
			
			if(gearPress.State(Stick.right.getPOV() != -1))
			{
				if(Stick.right.getPOV() == 0 && Gear < 4)
					Gear++;
				else if(Stick.right.getPOV() == 180 && Gear > 2)
					Gear--;
			}
			
			//Temporary Manual
			if(Stick.left.getPOV() == 0)
				pushSol.set(Value.kForward);
			else if(Stick.left.getPOV() == 180)
				pushSol.set(Value.kReverse);
		}
		
		DriveVal[0] *= (double)Gear/4.0;
		DriveVal[1] *= (double)Gear/4.0;
		
		chassis.drive(DriveVal[0], DriveVal[1]);
		
		
		if((XboxMode || demoMode) && Rumble)
		{
			Xbox.setRumble(RumbleType.kLeftRumble, Math.abs(DriveVal[0]));
			Xbox.setRumble(RumbleType.kRightRumble, Math.abs(DriveVal[1]));
		}
		else if(false)
		{
			if(Xbox.getXButton())
			{
				Xbox.setRumble(RumbleType.kLeftRumble, Math.abs(Xbox.getY(Hand.kLeft)));
				Xbox.setRumble(RumbleType.kRightRumble, Math.abs(Xbox.getY(Hand.kLeft)));
			}
			else
			{
				Xbox.setRumble(RumbleType.kLeftRumble, 0);
				Xbox.setRumble(RumbleType.kRightRumble, 0);
			}
		}
		
		
		if(Xbox.getStartButtonPressed())
		{
			cons.LoadConstants();
			MotorSetup();
		}
		
		if(Stick.right.getRawButtonPressed(3))
			intake.closeIntake();
		

		if(Stick.left.getPOV() == 0)
			pushSol.set(DoubleSolenoid.Value.kForward);
		else if(Stick.left.getPOV() == 180)
			pushSol.set(DoubleSolenoid.Value.kReverse);
	}
	
	@Override
	public void disabledPeriodic()
	{
		Xbox.setRumble(RumbleType.kLeftRumble, 0);
		Xbox.setRumble(RumbleType.kRightRumble, 0);
		
		String GameData = DriverStation.getInstance().getGameSpecificMessage();
		if(GameData != "")
		{
			FMSdata=GameData;
		}

		SmartDashboard.putString("FMS", FMSdata);
		
	}
	
	@Override
	public void testPeriodic()
	{
		
	}

	@Override
	public void robotPeriodic()
	{
		Pneum.DisplayPSI();
		
		SmartDashboard.putBoolean("Intake Open", intake.GetIntakePosition());
		SmartDashboard.putBoolean("ManualElevator", ManualElevator);
		SmartDashboard.putString("Set Position", elevator.GetSetPosition().toString());
		SmartDashboard.putNumber("Elevator Current", elevator.GetMotor().GetTalons()[0].getOutputCurrent());
		SmartDashboard.putNumber("Gearing", Gear-1);
		SmartDashboard.putString("Set Position", elevator.GetSetPosition().toString());
		Elevator.Position position = elevator.GetRealPosition();
		if(position != null)
			SmartDashboard.putString("Real Position", position.toString());
		else
			SmartDashboard.putString("Real Position", "Null");
	}

	public void CameraSetup()
	{
		try 
		{
			CameraServer.getInstance().startAutomaticCapture("cam0", 0);
			CameraServer.getInstance().startAutomaticCapture("cam1", 1);
		}
		catch (Exception e)
		{
			System.out.println("Camera Error: " + e.getMessage());
		}
	}
	
	public void MotorSetup()
	{
		CAN = new WPI_TalonSRX[cons.GetInt("CANLength")];
		for(int i = 0; i < CAN.length; i++)
		{
			CAN[i] = new WPI_TalonSRX(i);
		}
		
		Drive.SetFullCAN(CAN);
		
		int[] leftArr = {cons.GetInt("Left0"), cons.GetInt("Left1")};
		left = new Drive(leftArr);

		int[] rightArr = {cons.GetInt("Right0"), cons.GetInt("Right1")};
		right = new Drive(rightArr);
		
		int[] elevatorArr = {cons.GetInt("Elevator")};
		ElevatorDrive = new Drive(elevatorArr);
		
		int[] lIntakeArr = {cons.GetInt("lInt")};
		lIntake = new Drive(lIntakeArr);
		
		int[] rIntakeArr = {cons.GetInt("rInt")};
		rIntake = new Drive(rIntakeArr);
		
		int[] ClimberArr = {cons.GetInt("Climber")};
		Climber = new Drive(ClimberArr);
	}
	
}

