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
import MotorControllers.PneumaticsControl;

import Chassis.*;
import Controls.*;
import MotorControllers.*;
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
	Drive left, right, Elevator, Climber;
	Drive lIntake, rIntake;
	
	DoubleSolenoid intSol, StopSol;
	
	Encoder lEnc, rEnc;
	DigitalInput Upper, Middle, Lower, Stop;
	
	TankDrive chassis;
	Elevator elevator;
	Intake intake;
	Autonomous auto;
	PneumaticsControl Pneum;
	
	boolean XboxMode, demoMode, HalfSpeed, RedSide, Rumble;
	
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
		StopSol = new DoubleSolenoid(cons.GetInt("stopF"), cons.GetInt("stopR"));
		
		lEnc = new Encoder(cons.GetInt("lEnc1"), cons.GetInt("lEnc2"));
		rEnc = new Encoder(cons.GetInt("rEnc1"), cons.GetInt("rEnc2"));
		
		Upper = new DigitalInput(cons.GetInt("Upper"));
		Middle = new DigitalInput(cons.GetInt("Middle"));
		Lower = new DigitalInput(cons.GetInt("Lower"));
		Stop = new DigitalInput(cons.GetInt("Stop"));
		
		
		DigitalInput[] arr = {Lower, Middle, Upper};
		
		chassis = new TankDrive(left, right);
		elevator = new Elevator(Elevator, StopSol, Stop, arr);
		intake = new Intake(lIntake, rIntake, intSol);
		
		Pneum = new PneumaticsControl(0, cons.GetInt("PressureSensor"), 4.547368);
		
		Pneum.DisplayPSI();
		Pneum.SystemSwitch();
		
		//Internal Variables
		XboxMode = false;
		demoMode = false;
		HalfSpeed = false;
		Rumble = true;
		
		FMSdata = "LLL";
		
		CameraSetup();
		
		chooser.addDefault("Drive to Line", "line");
		chooser.addObject("Left", "left");
		chooser.addObject("Middle", "mid");
		chooser.addObject("Right", "right");
		chooser.addObject("Dance", "dance");
		
		SmartDashboard.putData("Auto choices", chooser);
		SmartDashboard.putBoolean("XboxMode", XboxMode);
		SmartDashboard.putBoolean("Demonstration", demoMode);
		SmartDashboard.putBoolean("HalfSpeed", HalfSpeed);
		SmartDashboard.putBoolean("Cube", true);
	}

	
	@Override
	public void autonomousInit() 
	{
		AutoSelected = SmartDashboard.getString("Auto choices", "line");
		
		boolean hasCube = SmartDashboard.getBoolean("Cube", false);
		
		auto = new Autonomous(chassis, intake, elevator, FMSdata, rEnc, lEnc);
		auto.hasCube = hasCube;
	}

	
	@Override
	public void autonomousPeriodic()
	{
		Pneum.SystemSwitch();
		
		switch(AutoSelected) {

		case "dance":
			auto.Dance();
			break;
			
		case "left":
				auto.Side('L');
				break;
				
		case "mid":
			auto.Middle();
			break;
			
		case "right":
			auto.Side('R');
			break;
			
		case "line":
		default:
			auto.DriveToLine();
			break;
		}
	}

	
	@Override
	public void teleopPeriodic()
	{
		Pneum.SystemSwitch();
		Pneum.DisplayPSI();
		
		//Get values
		XboxMode = SmartDashboard.getBoolean("XboxMode", false);
		demoMode = SmartDashboard.getBoolean("Demonstration",  false);

		double[] DriveVal;
		
		//Controls
		if(XboxMode)
		{
			DriveVal = Xbox.GetDrive();
			
			if(Xbox.getXButtonPressed())
				HalfSpeed = !HalfSpeed;
			
			elevator.Manual(Stick.right.getY());
			
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
		}
		else if(demoMode)
		{
			DriveVal = Xbox.GetDrive();
			
			if(Xbox.getXButtonPressed())
				HalfSpeed = !HalfSpeed;
			
			elevator.Manual(Xbox.getY(Hand.kRight));

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
		}
		else
		{
			if(Stick.right.getRawButton(4))
				DriveVal = Stick.GetPerfectStraight();
			else if(Stick.right.getRawButton(3))
				DriveVal = Stick.GetPerfectTurn();
			else
				DriveVal = Stick.GetDrive();
			
			
			if(Stick.right.getRawButtonPressed(2))
				HalfSpeed = !HalfSpeed;
			
			//Elevator
			if(Stick.right.getRawButton(1))
				elevator.Climb();
			else
				elevator.Manual(-Xbox.getY(Hand.kRight));
			
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
			
			//Temporary Manual
			if(Stick.left.getPOV() == 0)
				StopSol.set(Value.kForward);
			else if(Stick.left.getPOV() == 180)
				StopSol.set(Value.kReverse);
		}
		
		//Chassis
		if(HalfSpeed)
		{
			DriveVal[0] /= 2;
			DriveVal[1] /= 2;
		}
		
		chassis.drive(DriveVal[0], DriveVal[1]);
		
		
		if((XboxMode || demoMode) && Rumble)
		{
			Xbox.setRumble(RumbleType.kLeftRumble, Math.abs(DriveVal[0]));
			Xbox.setRumble(RumbleType.kRightRumble, Math.abs(DriveVal[1]));
		}
		else
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
		
		
		//Update SmartDashboard Values
		SmartDashboard.putBoolean("HalfSpeed", HalfSpeed);
		SmartDashboard.putBoolean("Intake Open", intake.GetIntakePosition());
	}
	
	@Override
	public void disabledPeriodic()
	{
		Xbox.setRumble(RumbleType.kLeftRumble, 0);
		Xbox.setRumble(RumbleType.kRightRumble, 0);

		Pneum.DisplayPSI();
		
		String GameData = DriverStation.getInstance().getGameSpecificMessage();
		if(GameData != "")
		{
			FMSdata=GameData;
		}
	}
	
	@Override
	public void testPeriodic()
	{
		
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
		Elevator = new Drive(elevatorArr);
		
		int[] lIntakeArr = {cons.GetInt("lInt")};
		lIntake = new Drive(lIntakeArr);
		
		int[] rIntakeArr = {cons.GetInt("rInt")};
		rIntake = new Drive(rIntakeArr);
		
		int[] ClimberArr = {cons.GetInt("Climber")};
		Climber = new Drive(ClimberArr);
	}
	
}

