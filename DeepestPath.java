import java.util.*;

class DeepestPath{
	
	public static void main(String[] args){
		System.out.println("Program to find the deepest Path");
		String path="dir\n\tsubdir1\n\t\tfile1.ext\n\t\tsubsubdir1\n\tsubdir2\n\t\tsubsubdir2\n\t\t\tfile2.ext";
		System.out.println("Path:\t"+path);
		printDeepestPath(path);
	}

	static void printDeepestPath(String path){

	}

	class Directory{
		public ArrayList<Directory> subDir;
		public String name;
		public int deepest;


		public Directory(String name){
			this.name=name;
			this.subDir=null;
			deepest=0;
		}

		


	}
}