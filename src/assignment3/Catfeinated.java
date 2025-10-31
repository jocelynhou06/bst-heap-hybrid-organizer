package assignment3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Catfeinated implements Iterable<Cat> {
	public CatNode root;

	public Catfeinated() {
	}

	public Catfeinated(CatNode dNode) {
		this.root = dNode;
	}

	// Constructor that makes a shallow copy of a Catfeinated cafe
	// New CatNode objects, but same Cat objects
	public Catfeinated(Catfeinated cafe) {
		/*
		 * TODO: ADD YOUR CODE HERE
		 */
		if(cafe.root != null){
			this.root = new CatNode(cafe.root.catEmployee);
			copyingTrees(cafe.root, this.root);
		}
		else{
			this.root = null;
		}

	}
	private void copyingTrees(CatNode n, CatNode r) {
		if (n.senior != null) {
			r.senior = new CatNode(n.senior.catEmployee);
			r.senior.parent = r.senior;
			copyingTrees(n.senior, r.senior);
		}
		if (n.junior != null) {
			r.junior = new CatNode(n.junior.catEmployee);
			r.junior.parent = r.junior;
			copyingTrees(n.junior, r.junior);
		}
	}

	// add a cat to the cafe database
	public void hire(Cat c) {
		if (root == null) 
			root = new CatNode(c);
		else
			root = root.hire(c);
	}

	// removes a specific cat from the cafe database
	public void retire(Cat c) {
		if (root != null)
			root = root.retire(c);
	}

	// get the oldest hire in the cafe
	public Cat findMostSenior() {
		if (root == null)
			return null;

		return root.findMostSenior();
	}

	// get the newest hire in the cafe
	public Cat findMostJunior() {
		if (root == null)
			return null;

		return root.findMostJunior();
	}

	// returns a list of cats containing the top numOfCatsToHonor cats 
	// in the cafe with the thickest fur. Cats are sorted in descending 
	// order based on their fur thickness. 
	public ArrayList<Cat> buildHallOfFame(int numOfCatsToHonor) {
		/*
		 * TODO: ADD YOUR CODE HERE
		 */

		int maxCats = this.getNumberOfCats();
		ArrayList<Cat> listOfFame = new ArrayList<>();
		ArrayList<Cat> traversalList = new ArrayList<>();
		for(Cat c : this){
			traversalList.add(c);
		}

		if(numOfCatsToHonor > maxCats){
			for(int i = 0; i < maxCats; i++){
				listOfFame.add(getFurriestCat(traversalList));
			}
		}
		else{
			for(int i = 0; i < numOfCatsToHonor; i++){
				listOfFame.add(getFurriestCat(traversalList));
			}
		}

		return listOfFame;
	}

	private int getNumberOfCats() {
		int number = 0;
		for(Cat c : this){
			number++;
		}
		return number;
	}
	private Cat getFurriestCat(ArrayList<Cat> traversalList){
		Cat maxFur = null;
		int indexOfMaxFur = 0;
		int index = 0;
		for(Cat c : traversalList){
			if(maxFur == null){
				maxFur = c;
				indexOfMaxFur = index;
			}
			else{
				if(c.getFurThickness() > maxFur.getFurThickness()){
					maxFur = c;
					indexOfMaxFur = index;
				}
			}
			index++;
		}
		return traversalList.remove(indexOfMaxFur);
	}

	// Returns the expected grooming cost the cafe has to incur in the next numDays days
	public double budgetGroomingExpenses(int numDays) {
		/*
		 * TODO: ADD YOUR CODE HERE - DONE
		 */
		double expectedExpenses = 0;

		for(Cat c: this){
			if(c.getDaysToNextGrooming() <= numDays){
				expectedExpenses += c.getExpectedGroomingCost();
			}
		}

		return expectedExpenses;
	}

	// returns a list of list of Cats. 
	// The cats in the list at index 0 need be groomed in the next week. 
	// The cats in the list at index i need to be groomed in i weeks. 
	// Cats in each sublist are listed in from most senior to most junior. 
	public ArrayList<ArrayList<Cat>> getGroomingSchedule() {
		/*
		 * TODO: ADD YOUR CODE HERE
		 */

		int max = -1;
		for(Cat c: this){
			if(c.getDaysToNextGrooming() > max){
				max = c.getDaysToNextGrooming();
			}
		}

		int weeks = (max / 7);

		ArrayList<ArrayList<Cat>> toReturn = new ArrayList<>();
		for(int i = 0; i <= weeks; i++){
			toReturn.add(new ArrayList<Cat>());
		}

		for(Cat c : this){
			int index = (c.getDaysToNextGrooming() / 7);
			int endOfList = toReturn.get(index).size();
			toReturn.get(index).add(endOfList, c);
		}

		/*for(int i = 0; i < toReturn.size(); i ++){
			System.out.println(toReturn.get(i));
		}*/
		return toReturn;
	}


	public Iterator<Cat> iterator() {
		return new CatfeinatedIterator();
	}

	public static class CatNode {
		public Cat catEmployee;
		public CatNode junior;
		public CatNode senior;
		public CatNode parent;

		public CatNode(Cat c) {
			this.catEmployee = c;
			this.junior = null;
			this.senior = null;
			this.parent = null;
		}

		// add the c to the tree rooted at this and returns the root of the resulting tree
		public CatNode hire(Cat c) {
			/*
			 * TODO: ADD YOUR CODE HERE
			 */

			CatNode curr = this;

			if (c.getMonthHired() > curr.catEmployee.getMonthHired()) {
				if (curr.junior == null) {
					curr.junior = new CatNode(c);
					curr.junior.parent = curr;
				}
				else {
					curr.junior = curr.junior.hire(c);
				}
				if (tempName(curr.junior)) {
					return rightRotation(curr.junior);
				}
			} else if (c.getMonthHired() < curr.catEmployee.getMonthHired()) {
				if (curr.senior == null) {
					curr.senior = new CatNode(c);
					curr.senior.parent = curr;
				}
				else {
					curr.senior = curr.senior.hire(c);
				}
				if (tempName(curr.senior)) {
					return leftRotation(curr.senior);
				}
			}

			return curr;
		}

		public boolean tempName(CatNode c) {
			if (c.catEmployee.getFurThickness() > c.parent.catEmployee.getFurThickness()) {
				return true;
			}
			return false;
		}

		private CatNode rightRotation(CatNode c) {
			CatNode temp = c.parent;
			CatNode originalTemp = c.senior;

			c.senior = temp;
			temp.junior = originalTemp;
			if (originalTemp != null) {
				originalTemp.parent = temp;
			}

			c.parent = temp.parent;

			if (temp.parent != null) {
				//System.out.println("right: made it here?");
				if (temp.parent.junior == temp) {
					temp.parent.junior = c;
				} else {
					temp.parent.senior = c;
				}
			}
			temp.parent = c;

			return c;
		}

		private CatNode leftRotation(CatNode c) {
			CatNode temp = c.parent;
			CatNode originalTemp = c.junior;

			c.junior = temp;

			temp.senior = originalTemp;
			if (originalTemp != null) {
				originalTemp.parent = temp;
			}

			c.parent = temp.parent;

			if (temp.parent != null) {
				//System.out.println("left: made it here?");
				if (temp.parent.junior == temp) {
					temp.parent.junior = c;
				} else {
					temp.parent.senior = c;
				}
			}

			temp.parent = c;

			return c;
		}

		// remove c from the tree rooted at this and returns the root of the resulting tree
		public CatNode retire(Cat c) {
			/*
			 * TODO: ADD YOUR CODE HERE
			 */

			CatNode curr = this;

			if (c.getMonthHired() > curr.catEmployee.getMonthHired()) {
				if(curr.junior != null){
					curr.junior = curr.junior.retire(c);
				}
			}
			else if (c.getMonthHired() < curr.catEmployee.getMonthHired()) {
				if(curr.senior != null){
					curr.senior = curr.senior.retire(c);
				}
			}
			else if (c.getMonthHired() == curr.catEmployee.getMonthHired()){
				if(curr.senior == null && curr.junior == null){
					if(curr.parent != null) {
						if (curr.parent.senior == curr) {
							curr.parent.senior = null;
						}
						else if (curr.parent.junior == curr) {
							curr.parent.junior = null;
						}
					}
					return null;
				}
				else if(curr.senior != null && curr.junior == null){
					CatNode replacement = curr.senior;
					replacement.parent = curr.parent;
					if(curr.parent != null){
						if (curr.parent.junior == curr) {
							curr.parent.junior = replacement;
						}
						else {
							curr.parent.senior = replacement;
						}
					}

					if(retireFurChecker(replacement) == 1){
						return rightRotation(replacement.junior);
					}
					else if(retireFurChecker(replacement) == 2){
						return leftRotation(replacement.senior);
					}
					return replacement;
				}
				else if(curr.senior == null && curr.junior != null){
					CatNode replacement = curr.junior;
					replacement.parent = curr.parent;
					if(curr.parent != null){
						if (curr.parent.senior == curr) {
							curr.parent.senior = replacement;
						}
						else {
							curr.parent.junior = replacement;
						}
					}

					if(retireFurChecker(replacement) == 1){
						return rightRotation(replacement.junior);
					}
					else if(retireFurChecker(replacement) == 2){
						return leftRotation(replacement.senior);
					}
					return replacement;
				}
				else if(curr.senior != null && curr.junior != null){
					CatNode replacement = curr.junior;
					while(replacement.senior != null){
						replacement = replacement.senior;
					}

					if(replacement.parent != null){
						if(replacement.parent.senior == replacement){
							replacement.parent.senior = null;
						}
						else if(replacement.parent.junior == replacement){
							replacement.parent.junior = null;
						}

					}

					replacement.parent = curr.parent;
					if(replacement.parent != null){
						if (replacement.parent.senior == curr) {
							replacement.parent.senior = replacement;
						}
						else if(replacement.parent.junior == curr){
							replacement.parent.junior = replacement;
						}
					}

					replacement.senior = curr.senior;
					if(replacement.senior != null){
						replacement.senior.parent = replacement;
					}
					replacement.junior = curr.junior;
					if(replacement.junior != null){
						replacement.junior.parent = replacement;
					}


					if(retireFurChecker(replacement) == 1){
						return rightRotation(replacement.junior);
					}
					else if(retireFurChecker(replacement) == 2){
						return leftRotation(replacement.senior);
					}
					return replacement;
				}
			}

			return curr;
		}

		public int retireFurChecker(CatNode c) {
			if(c.junior != null){
				if(c.catEmployee.getFurThickness() < c.junior.catEmployee.getFurThickness()){
					return 1;
				}
			}
			else if(c.senior != null){
				if(c.catEmployee.getFurThickness() < c.senior.catEmployee.getFurThickness()){
					return 2;
				}
			}
			return 0;
		}

		// find the cat with highest seniority in the tree rooted at this
		public Cat findMostSenior() {
			/*
			 * TODO: ADD YOUR CODE HERE - DONE
			 */

			CatNode curr = this;
			while (curr.senior != null) {
				curr = curr.senior;
			}
			return curr.catEmployee;
		}

		// find the cat with lowest seniority in the tree rooted at this
		public Cat findMostJunior() {
			/*
			 * TODO: ADD YOUR CODE HERE - DONE
			 */
			CatNode curr = this;
			while (curr.junior != null) {
				curr = curr.junior;
			}
			return curr.catEmployee;
		}

		// Feel free to modify the toString() method if you'd like to see something else displayed.
		public String toString() {
			String result = this.catEmployee.toString() + "\n";
			if (this.junior != null) {
				result += "junior than " + this.catEmployee.toString() + " :\n";
				result += this.junior.toString();
			}
			if (this.senior != null) {
				result += "senior than " + this.catEmployee.toString() + " :\n";
				result += this.senior.toString();
			}
			if (this.parent != null) {
				result += "parent of " + this.catEmployee.toString() + " :\n";
				result += this.parent.catEmployee.toString() +"\n";
			}
			return result;
		}
	}

	public class CatfeinatedIterator implements Iterator<Cat> {
		// HERE YOU CAN ADD THE FIELDS YOU NEED
		private CatNode cur;
		private ArrayList<CatNode>  visited;
		public CatfeinatedIterator() {
			/*
			 * TODO: ADD YOUR CODE HERE - DONE
			 */
			if (root == null) {
				throw new IllegalArgumentException("Root cannot be null");
			}
			cur = root;
			visited = new ArrayList<CatNode>();
			visitingTrees(cur);
		}
		private void visitingTrees(CatNode node) {
			if (node.senior != null) {
				visitingTrees(node.senior);
			}
			visited.add(node);
			if (node.junior != null) {
				visitingTrees(node.junior);
			}

		}
		public Cat next() {
			if (!hasNext()){
				throw new NoSuchElementException("No more elements to iterate on");
			}

			return visited.remove(visited.size() - 1).catEmployee;
		}
		public boolean hasNext() {
			/*
			 * TODO: ADD YOUR CODE HERE - DONE
			 */

			return visited.size() != 0;
		}
	}


	public static void main(String[] args) {
		/*Cat A = new Cat("A", 30, 50, 5, 85.0);
		Cat B = new Cat("B", 20, 30, 2, 250.0);
		Cat C = new Cat("C", 15, 35, 12, 30.0);
		Cat D = new Cat("D", 40, 15, 12, 30.0);
		Cat E = new Cat("E", 35, 20, 20, 30.0);*/

		Catfeinated test = new Catfeinated();
		/*Cat A = new Cat("A", 5, 10, 5, 85.0);
		Cat B = new Cat("B", 7, 10, 2, 250.0);
		Cat C = new Cat("C", 8, 10, 12, 30.0);
		Cat D = new Cat("D", 6, 10, 12, 30.0);
		Cat E = new Cat("E", 3, 10, 20, 30.0);
		Cat F = new Cat("F", 4, 10, 12, 30.0);
		Cat G = new Cat("G", 1, 10, 20, 30.0);
		test.hire(A);
		test.hire(B);
		test.hire(C);
		test.hire(D);
		test.hire(E);
		test.hire(F);
		test.hire(G);
		System.out.println(test.root.toString());
		System.out.println("break");
		//System.out.println(test.root.toString());
		test.root.retire(E);
		System.out.println("break");
		//System.out.println(test.root.junior.retire(B));
		System.out.println(test.root.toString());
		//System.out.println(test.root.senior.retire(E));
		/*System.out.println("Root b4 hire A: " + test.root);
		test.hire(A);
		//System.out.println(test.root.toString());
		System.out.println("Root b4 hire B: " + test.root);
		test.hire(B);
		//System.out.println(test.root.toString());
		System.out.println("Root b4 hire C: " + test.root);
		test.hire(C);
		System.out.println(test.root.parent);
		System.out.println(test.root.junior);
		System.out.println(test.root.senior);

		//System.out.println(test.root.toString());
		System.out.println("Root b4 hire D: " + test.root);
		test.hire(D);
		System.out.println("Root b4 hireE: " + test.root);
		test.hire(E);
		test.getGroomingSchedule();
		//System.out.println("Root after hire: " + test.root);
		/*System.out.println(test.root.parent);
		System.out.println(test.root.junior);
		System.out.println(test.root.senior);*/
		//System.out.println(test.root.toString());
		//System.out.println(test.root.senior.toString());
		//System.out.println(test.root.senior.toString());*/*/

		/*Cat B = new Cat("Buttercup", 45 ,53, 5, 85);
		Cat C = new Cat("Chessur", 8, 23, 2, 250.0);
		Cat J = new Cat("Jonesy", 0, 21, 12, 30.0);
		Cat JJ = new Cat("JIJI", 156, 17, 1, 30.0);
		Cat JTO = new Cat("J. Thomas O’Malley", 21, 10, 9, 20.0);
		Cat MrB = new Cat("Mr. Bigglesworth", 71, 0, 31, 55.0);
		Cat MrsN = new Cat("Mrs. Norris", 100, 68, 15, 115.0);
		Cat T = new Cat("Toulouse", 180, 37, 14, 25.0);

		test.hire(B);
		test.hire(JTO);
		test.hire(C);
		test.retire(B);
		test.hire(JJ);
		test.hire(J);
		test.hire(MrsN);
		test.retire(MrsN);
		System.out.println(test.root.toString());*/
	}
}