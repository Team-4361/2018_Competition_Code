package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

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
	Drive left, right, Elevator, lIntake, rIntake, Climber;
	
	TankDrive chassis;
	Elevator elevator;
	
	boolean XboxMode, HalfSpeed;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
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
		
		WPI_TalonSRX[] leftArr = {CAN[0], CAN[0]};
		left = new Drive(leftArr);

		WPI_TalonSRX[] rightArr = {CAN[0], CAN[0]};
		right = new Drive(rightArr);
		
		WPI_TalonSRX[] elevatorArr = {CAN[0]};
		Elevator = new Drive(elevatorArr);
		
		WPI_TalonSRX[] lIntakeArr = {CAN[0]};
		lIntake = new Drive(lIntakeArr);
		
		WPI_TalonSRX[] rIntakeArr = {CAN[0]};
		rIntake = new Drive(rIntakeArr);
		
		WPI_TalonSRX[] ClimberArr = {CAN[0]};
		Climber = new Drive(ClimberArr);
		
		chassis = new TankDrive(left, right);
		elevator = new Elevator(Elevator, rIntake, lIntake);
		
		//Internal Variables
		XboxMode = false;
		HalfSpeed = false;
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() 
	{
		AutoSelected = "";
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic()
	{
		
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic()
	{
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
			elevator.Intake();
		else if(Xbox.getBumper(Hand.kRight))
			elevator.Intake();
		else
			elevator.StopIntake();
		
		//Chassis
		if(HalfSpeed)
		{
			DriveVal[0] /= 2;
			DriveVal[1] /= 2;
		}
		
		chassis.drive(DriveVal[0], DriveVal[1]);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic()
	{
		
	}
}

