package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import  edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.VictorSP;

import edu.wpi.cscore.UsbCamera; 
import edu.wpi.first.cameraserver.CameraServer; 

import edu.wpi.first.wpilibj.DoubleSolenoid; 
import edu.wpi.first.wpilibj.DoubleSolenoid.Value; 
//import edu.wpi.first.wpilibj.Solenoid; 

//import edu.wpi.first.wpilibj.Encoder; 

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  

  private DifferentialDrive m_robotdrive;
  private Joystick m_driver;
  private Joystick m_operator;
  private static final int leftmainDeviceID = 1; 
  private static final int rightmainDeviceID = 2;
  private static final int leftfollowDeviceID = 3; 
  private static final int rightfollowDeviceID = 4;
  private CANSparkMax m_leftmainMotor;
  private CANSparkMax m_rightmainMotor;
  private CANSparkMax m_leftfollowMotor;
  private CANSparkMax m_rightfollowMotor;

  VictorSP m_shooter = new VictorSP(0);
  VictorSP m_shootConv = new VictorSP(1);
  VictorSP m_intakeConv = new VictorSP(2);
  VictorSP m_intake = new VictorSP(2);

   private CANEncoder m_leftencoder;
   private CANEncoder m_rightencoder;

  DoubleSolenoid intakeArm = new DoubleSolenoid(0,1);

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
   

    m_leftmainMotor = new CANSparkMax(leftmainDeviceID, MotorType.kBrushless);
    m_rightmainMotor = new CANSparkMax(rightmainDeviceID, MotorType.kBrushless);
    m_leftfollowMotor = new CANSparkMax(leftfollowDeviceID, MotorType.kBrushless);
    m_rightfollowMotor = new CANSparkMax(rightfollowDeviceID, MotorType.kBrushless);

    m_leftmainMotor.restoreFactoryDefaults();
    m_rightmainMotor.restoreFactoryDefaults();
    m_leftfollowMotor.restoreFactoryDefaults();
    m_rightfollowMotor.restoreFactoryDefaults();

    m_robotdrive = new DifferentialDrive(m_leftmainMotor, m_rightmainMotor);

    m_driver = new Joystick(0);
    m_operator = new Joystick(1);

    m_leftfollowMotor.follow(m_leftmainMotor);
    m_rightfollowMotor.follow(m_rightmainMotor);

    m_leftencoder = m_leftmainMotor.getEncoder();
    m_rightencoder = m_rightmainMotor.getEncoder();

    UsbCamera camera1 = CameraServer.getInstance().startAutomaticCapture(0); 
      camera1.setResolution(256, 144); 
      camera1.setFPS(30); 
      camera1.setExposureAuto(); 

  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
 
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
   
    }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    double multiplier = 0.8; //Base Speed
    if(m_driver.getRawButton(5)) { 
      multiplier = 1.0; 
    } //Drive base speed multiplier FAST
    if(m_driver.getRawButton(6)) { 
      multiplier = 0.6; 
    } //Drive base speed multiplier SLOW

    double drivePower = -multiplier * m_driver.getRawAxis(1); 
    m_robotdrive.arcadeDrive(drivePower, multiplier * m_driver.getRawAxis(4));

    SmartDashboard.putNumber("Left Encoder Position", m_leftencoder.getPosition());
    SmartDashboard.putNumber("Right Encoder Position", m_rightencoder.getPosition());
    SmartDashboard.putNumber("Left Encoder Velocity", m_leftencoder.getVelocity());
    SmartDashboard.putNumber("Right Encoder Velocity", m_rightencoder.getVelocity());
    //displays drive base encoder values.

    if(m_operator.getPOV() == 0){ 
      //D-PAD UP arm up
      intakeArm.set(Value.kReverse); 
      }else if (m_operator.getPOV() == 180){ 
      //D-PAD DOWN arm down
      intakeArm.set(Value.kForward); 
      }//Intake arm up and down

    if(m_operator.getRawButton(2)) { // PICK BUTTON if boton held it SHOOTS shooter and shoter and intak convayer spin
        m_shooter.set(1);
        m_shootConv.set(1);
        m_intakeConv.set(1);
      } else if (m_operator.getRawButton(1)) { // PICK BUTTON if held it INTAKES 
        m_intake.set(1);
        m_intakeConv.set(1);
      } else { //it turns off
        m_shooter.set(0);
        m_shootConv.set(0);
        m_intakeConv.set(0); 
        m_intake.set(0);
      }

    /* if(m_operator.getRawButton(2)) { // PICK BUTTON if boton held shoot 
        m_shooter.set(1);
      } else if (m_operator.getRawButton(1)) { // PICK BUTTON reverse shooter 
        m_shooter.set(-1);
      } else { 
        m_shooter.set(0);
      }

    if(m_operator.getRawButton(4)) { //PICK BUTTON move ball shooter convayer up
        m_shootConv.set(1);
      } else if (m_operator.getRawButton(3)) { // PICK BUTTON reverse shotter convayer
        m_shootConv.set(-1);
      } else { 
        m_shootConv.set(0);
      }

    if(m_operator.getRawButton(5)) { //PICK BUTTON move ball intake convayer up 
        m_intakeConv.set(1);
      } else if (m_operator.getRawButton(6)) { // PICK BUTTON reverse intake convayer
        m_intakeConv.set(-1);
      } else { 
        m_intakeConv.set(0);
      }

     if(m_operator.getRawButton(7)) { //PICK BUTTON move ball intake
        m_intake.set(1);
      } else if (m_operator.getRawButton(8)) { //PICK BUTTON reverse intake 
        m_intake.set(-1);
      } else { 
        m_intake.set(0);
      }  */
 
  }


  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}