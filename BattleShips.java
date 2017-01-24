/**battle ships on board
X..X
...X
...X

	[0][0]
[1][0] [0][1]

**/
import java.util.*;
class BattleShips{

	public static void main(String[] args){
		System.out.println("Program to find battleShips");
		char[][] board={	{'X','.','.','X'},
							{'.','.','.','X'},
							{'.','.','.','X'}};
		printBoard(board);
		countShips(board);
		}
		class Points{
			int x,y;
			public Points(int x,int y){
				this.x=x;
				this.y=y;
			}
			public String toString(){
				return "{"+x+","+y+"}";
			}
		}
		public int countShips(char board[][]){
			if(board==null|| board.length<1|| board[0].length<1) return 0;
			Queue<BattleShips.Points> q= new LinkedList<BattleShips.Points>();
			q.add(this.new Points(board[0],board[1]));
			

			return 0;
		}
		public static void printBoard(char[][] board){
			if(board==null || board.length<1|| board[0].length<1) return;
			for(char[] c:board){
				System.out.print("|");
				for(char points:c){
					System.out.print(points+" ");
				}
				System.out.println("|\n");
			}
		}
	}
