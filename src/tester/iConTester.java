package tester;

import java.util.LinkedList;

public class iConTester {
	public LinkedList<TestCase> tests = new LinkedList<TestCase>();
	
	public void execute(int type){
		if(type==-1){//run all tests
			for(int i=0;i<tests.size();i++){
				tests.get(i).execute();
			}
		}
	}
	
	public void initialize(){
		/*
		 * create test cases
		 */
		AddUserTestCase testcase1 = new AddUserTestCase();
		tests.add(testcase1);
		
	}
	
	public static void main(String args[]){
		iConTester tester = new iConTester();
		tester.initialize();
		tester.execute(-1);
	}
}