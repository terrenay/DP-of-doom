import java.util.Arrays;
import java.util.LinkedList;

public class t3 {

	public static void main(String[] args) {
		int[] arr = new int[] { 1, 7, 5, 10, 2, 3, 2 };

		LinkedList<Integer> list1 = new LinkedList<Integer>();
		LinkedList<Integer> list2 = new LinkedList<Integer>();
		LinkedList<Integer> list3 = new LinkedList<Integer>();
		//"allowed" will keep track of which elements are already in a subset (0) and which ones are still available (1)
		int[] allowed = new int[arr.length];
		for (int i = 0; i < arr.length; i++)
			allowed[i] = 1;

		int totalSum = Arrays.stream(arr).reduce((a, b) -> a + b).getAsInt();
		int maxElement = Arrays.stream(arr).reduce((a, b) -> a > b ? a : b).getAsInt();

		// If some element is greater than totalSum/3, the subset which contains this
		// element will have a sum greater than 
		// totalSum/3, so we can't split arr into 3 equal subsets.
		if (totalSum % 3 != 0 || maxElement > totalSum / 3) {
			System.out.println("Keine Partition");
			return;
		} else {
			// Each call changes "allowed" by setting the newly partitioned elements to 0.
			list1 = elementsSubArrayWithSumK(arr, allowed, totalSum / 3);
			list2 = elementsSubArrayWithSumK(arr, allowed, totalSum / 3);
			list3 = elementsSubArrayWithSumK(arr, allowed, totalSum / 3);
		}

		//If there are still elements remaining in the "allowed" array, this means
		//that they could not be partitioned.
		int remaining = Arrays.stream(allowed).reduce((a, b) -> a + b).getAsInt();
		if (remaining == 0) {
			System.out.println(
					"Partition gefunden: " + list1.toString() + ", " + list2.toString() + ", " + list3.toString());
		} else {
			System.out.println("Keine Partition");
		}

	}

	/**
	 * Returns a list of elements of arr, such that the elements of this list form a subset which sums to k.
	 * @param allowed allowed[i] = 1 if and only if arr[i] may be used in a subset.
	 * @return Either a list of elements of arr if a subset is found, or an empty list otherwise.
	 */
	private static LinkedList<Integer> elementsSubArrayWithSumK(int[] arr, int[] allowed, int k) {
		int n = arr.length;
		LinkedList<Integer> output = new LinkedList<Integer>();
		int[][] dp = new int[n + 1][k + 1];
		
		//This function is basically SUBSETSUM with backtracking (to find the partition).
		
		//Base Case: 
		for (int i = 1; i <= n; i++) {
			//If an element is not allowed, we skip its base case
			if (allowed[i - 1] == 1 && arr[i - 1] <= k) { // Offsets by 1 are because dp has size [n+1][k+1]
				dp[i][arr[i - 1]] = 1;
			}
		}
		
		//Main Loop
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= k; j++) {
				//Main dp logic: (1) Ignore the new number and copy the value from the line (in the dp table) above it, 
				//or (2) keep the value set by the base case, or (3) add the new number to subset.
				//Since "allowed" and "dp" only contain the values 0 and 1, taking the maximum 
				//corresponds to a logical OR.
				dp[i][j] = Math.max(dp[i - 1][j],
						Math.max(dp[i][j], allowed[i - 1] == 1 ? dp[i - 1][Math.max(0, j - arr[i - 1])] : 0));
			}
			if (dp[i][k] == 1) {
				//Everything from here on is just backtracking.
				int curRow = i;
				int curCol = k;
				while (curRow > 1 && curCol > 0) {
					if (allowed[curRow - 1] == 1 && (dp[curRow - 1][Math.max(0, curCol - arr[curRow - 1])] == 1
							|| curCol - arr[curRow - 1] == 0)) {
						output.add(arr[curRow - 1]); 
						//Since arr[curRow-1] is now definitely in this subset, we change the value in "allowed" to 0.
						allowed[curRow - 1] = 0; 
						curCol -= arr[curRow - 1];
					}
					curRow--;
				}
				//Ugly special case for the first element, basically ignore this lmao.
				if (dp[curRow][curCol] == 1) { 
					output.add(arr[curRow - 1]);
					allowed[curRow - 1] = 0;
				}
				break;
			}
		}
		return output;
	}
}
