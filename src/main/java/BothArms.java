/**
	 * Basic wrapper class for two arms
	 */
	public class BothArms{
		private Arm leftArm;
		private Arm rightArm;
		
		/**
		 * Basic wrapper class for two arms
		 * @param leftArm the left arm
		 * @param rightArm the right arm
		 */
		public BothArms(Arm leftArm, Arm rightArm){
			this.leftArm = leftArm;
			this.rightArm = rightArm;
		}
		
		/**
		 * Returns the left Arm object
		 */
		public Arm getLeftArm(){
			return leftArm;
		}
		
		/**
		 * Returns the right Arm object
		 */
		public Arm getRightArm(){
			return rightArm;
		}
	}