package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Compressor;

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

	JoystickTank Stick;
	XboxArcade Xbox;
	
	WPI_TalonSRX[] CAN;
	Drive left, right, Elevator, Climber;
	Drive lIntake, rIntake;
	
	DoubleSolenoid lSol, rSol;
	
	TankDrive chassis;
	Elevator elevator;
	Intake intake;
	Autonomous auto;
	
	Compressor comp;
	
	boolean XboxMode, HalfSpeed, RedSide;
	
	SendableChooser<String> chooser = new SendableChooser<>();
	
	
	@Override
	public void robotInit()
	{
		//Physical Peripherals
		Stick = new JoystickTank(0, 1);
		Xbox = new XboxArcade(3, Hand.kLeft);
		
		CAN = new WPI_TalonSRX[9];
		for(int i = 0; i < CAN.length; i++)
		{
			CAN[i] = new WPI_TalonSRX(i);
		}
		
		Drive.SetFullCAN(CAN);
		
		int[] leftArr = {0, 0};
		left = new Drive(leftArr);

		int[] rightArr = {0, 0};
		right = new Drive(rightArr);
		
		int[] elevatorArr = {0};
		Elevator = new Drive(elevatorArr);
		
		int[] lIntakeArr = {0};
		lIntake = new Drive(lIntakeArr);
		
		int[] rIntakeArr = {0};
		rIntake = new Drive(rIntakeArr);
		
		int[] ClimberArr = {0};
		Climber = new Drive(ClimberArr);
		
		lSol = new DoubleSolenoid(0, 0);
		rSol = new DoubleSolenoid(0, 0);
		
		chassis = new TankDrive(left, right);
		elevator = new Elevator(Elevator);
		intake = new Intake(lIntake, rIntake, lSol, rSol);
		
		comp = new Compressor(0);
		
		//Internal Variables
		XboxMode = false;
		HalfSpeed = false;
		
		chooser.addDefault("Drive to Line", "line");
		chooser.addObject("Dance", "dance");
		chooser.addObject("Switch", "switch");
		chooser.addObject("Scale", "scale");
		chooser.addObject("Switch*2", "2switch");
		
		SmartDashboard.putData("Auto choices", chooser);
		SmartDashboard.putBoolean("RedSide", false);
		SmartDashboard.putBoolean("XboxMode", XboxMode);
		SmartDashboard.putBoolean("HalfSpeed", HalfSpeed);
	}

	
	@Override
	public void autonomousInit() 
	{
		AutoSelected = SmartDashboard.getString("Auto choices", "line");
		RedSide = SmartDashboard.getBoolean("RedSide", false);
		auto = new Autonomous(chassis, RedSide, null, null);
	}

	
	@Override
	public void autonomousPeriodic()
	{
		switch(AutoSelected) {

		case "switch":
			auto.Dance();
			break;
			
		case "scale":
			auto.Dance();
			break;
			
		case "2switch":
			auto.Dance();
			break;
			
		case "dance":
			auto.Dance();
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
		//Get values
		XboxMode = SmartDashboard.getBoolean("XboxMode", false);

		double[] DriveVal;
		
		//Controls
		if(XboxMode)
		{
			DriveVal = Xbox.GetDrive();
			
			if(Xbox.getBButtonPressed())
				HalfSpeed = !HalfSpeed;
		}
		else
		{
			DriveVal = Stick.GetDrive();
			
			if(Stick.right.getRawButtonPressed(2))
				HalfSpeed = !HalfSpeed;
		}
		
		//Elevator
		elevator.Manual(Xbox.getY(Hand.kRight));
		
		if(Xbox.getBumper(Hand.kLeft))
			intake.intake();
		else if(Xbox.getBumper(Hand.kRight))
			intake.outtake();
		else
			intake.stopIntake();
		
		//Chassis
		if(HalfSpeed)
		{
			DriveVal[0] /= 2;
			DriveVal[1] /= 2;
		}
		
		Xbox.setRumble(RumbleType.kLeftRumble, Math.abs(Xbox.getX(Hand.kRight)));
		Xbox.setRumble(RumbleType.kRightRumble, Math.abs(Xbox.getX(Hand.kRight)));
		
		chassis.drive(DriveVal[0], DriveVal[1]);
		
		//Update SmartDashboard Values
		SmartDashboard.putBoolean("HalfSpeed", HalfSpeed);
	}
	
	@Override
	public void testPeriodic()
	{
		
	}
}

