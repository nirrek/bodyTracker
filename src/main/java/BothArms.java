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
		
		public Arm getLeftArm(){
			return leftArm;
		}
		
		public Arm getRightArm(){
			return rightArm;
		}
	}