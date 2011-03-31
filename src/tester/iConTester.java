package tester;

import java.util.LinkedList;

public class iConTester {
	public LinkedList<TestCase> tests = new LinkedList<TestCase>();
	
	public void execute(int type){
		if(type==-1){//run all tests
			for(int i=0;i<tests.size();i++){
				tests.get(i).execute();
			}
		}else{
			tests.get(type).execute();
		}
	}
	
	public void initialize(){
		/*
		 * create test cases
		 */
		AddUserTestCase testcase1 = new AddUserTestCase();
		tests.add(testcase1);
		MoveUserTestCase testcase2 = new MoveUserTestCase();
		tests.add(testcase2);
		LocateCoverageTestCase testcase3 = new LocateCoverageTestCase();
		tests.add(testcase3);
		RemoveUserTestCase testcase4 = new RemoveUserTestCase();
		tests.add(testcase4);
	}
	
	public static void main(String args[]){
		iConTester tester = new iConTester();
		tester.initialize();
		tester.execute(2);
	}
}
