package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;

import MotorControllers.Drive;
import Util.*;

public class Intake
{
	private Drive lInt, rInt;
	DoubleSolenoid intSol;
	
	double LintakeSpeed, RintakeSpeed, outtakeSpeed, fastOuttakeSpeed, stopIntakeSpeed;
	
	boolean IntakeOpen, cubeFixRun;
	Timer timer;
	
	public Intake(Drive lInt, Drive rInt, DoubleSolenoid intSol)
	{
		this.lInt = lInt;
		this.rInt = rInt;
		
		this.intSol = intSol;
		
		Constants cons = Constant.AllConstant;
		
		LintakeSpeed = cons.GetDouble("LintakeSpeed");
		RintakeSpeed = cons.GetDouble("RintakeSpeed");
		outtakeSpeed = cons.GetDouble("outtakeSpeed");
		fastOuttakeSpeed = cons.GetDouble("fastOuttakeSpeed");
		stopIntakeSpeed = cons.GetDouble("stopIntakeSpeed");
		
		IntakeOpen = false;
		cubeFixRun = false;
		timer = new Timer();
	}
	
	public void intake()
	{
		lInt.drive(-LintakeSpeed);
		rInt.drive(RintakeSpeed);
	}
	
	public void outtake()
	{
		lInt.drive(-outtakeSpeed);
		rInt.drive(outtakeSpeed);
	}
	
	public void fastOuttake()
	{
		lInt.drive(-fastOuttakeSpeed);
		rInt.drive(fastOuttakeSpeed);
	}
	
	public void stopIntake()
	{
		lInt.drive(stopIntakeSpeed);
		rInt.drive(-stopIntakeSpeed);
	}
	
	public void openIntake()
	{
		intSol.set(DoubleSolenoid.Value.kForward);

		IntakeOpen = true;
	}
	
	public void closeIntake()
	{
		intSol.set(DoubleSolenoid.Value.kReverse);
		
		IntakeOpen = false;
	}
	
	public void NoPressureIntake()
	{
		intSol.set(DoubleSolenoid.Value.kOff);
	}
	
	public void SwitchIntake()
	{
		if(IntakeOpen)
		{
			closeIntake();
		}
		else
		{
			openIntake();
		}
	}
	
	public boolean GetIntakePosition()
	{
		return IntakeOpen;
	}
	
	public void CubeFix(boolean button)
	{
		if(button && !cubeFixRun)
		{
			outtake();
			timer.reset();
			timer.start();
			cubeFixRun = true;
		}
		
		if(timer.get() >= 1)
		{
			if(rInt.GetSpeed() == outtakeSpeed)
			{
				intake();
				timer.stop();
				timer.reset();
				timer.start();
			}
			else if(rInt.GetSpeed() == RintakeSpeed)
			{
				stopIntake();
				timer.stop();
				timer.reset();
				cubeFixRun = false;
			}
			
		}
	}
}
