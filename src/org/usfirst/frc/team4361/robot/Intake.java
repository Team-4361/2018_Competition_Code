package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import MotorControllers.Drive;
import Util.Constants;

public class Intake
{
	private Drive lInt, rInt;
	DoubleSolenoid lSol, rSol;
	
	double intakeSpeed, outtakeSpeed;
	
	public Intake(Drive lInt, Drive rInt, DoubleSolenoid lSol, DoubleSolenoid rSol)
	{
		this.lInt = lInt;
		this.rInt = rInt;
		
		this.lSol = lSol;
		this.rSol = rSol;
		
		Constants cons = new Constants();
		cons.LoadConstants();
		
		intakeSpeed = cons.GetDouble("intakeSpeed");
		outtakeSpeed = cons.GetDouble("outtakeSpeed");
	}
	
	public void intake()
	{
		lInt.drive(intakeSpeed);
		rInt.drive(intakeSpeed);
	}
	
	public void outtake()
	{
		lInt.drive(outtakeSpeed);
		rInt.drive(outtakeSpeed);
	}
	
	public void stopIntake()
	{
		lInt.drive(0);
		rInt.drive(0);
	}
	
	public void openInttake()
	{
		lSol.set(DoubleSolenoid.Value.kForward);
		rSol.set(DoubleSolenoid.Value.kForward);
	}
	
	public void closeInttake()
	{

		lSol.set(DoubleSolenoid.Value.kReverse);
		rSol.set(DoubleSolenoid.Value.kReverse);
	}
}
