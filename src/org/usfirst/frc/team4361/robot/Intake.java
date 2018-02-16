package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import MotorControllers.Drive;
import Util.*;

public class Intake
{
	private Drive lInt, rInt;
	DoubleSolenoid intSol;
	
	double LintakeSpeed, RintakeSpeed, outtakeSpeed, fastOuttakeSpeed;
	
	boolean IntakeOpen = false;
	
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
		lInt.drive(0);
		rInt.drive(0);
	}
	
	public void openIntake()
	{
		intSol.set(DoubleSolenoid.Value.kForward);
	}
	
	public void closeIntake()
	{

		intSol.set(DoubleSolenoid.Value.kReverse);
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
		
		IntakeOpen = !IntakeOpen;
	}
	
	public boolean GetIntakePosition()
	{
		return IntakeOpen;
	}
}
