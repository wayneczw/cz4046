
class Chen_Zhiwei_Player extends Player {
	static int[][][] payoff = {  
		{{6,3},  //payoffs when first and second players cooperate 
		 {3,0}}, //payoffs when first player coops, second defects
		{{8,5},  //payoffs when first player defects, second coops
		 {5,2}}};//payoffs when first and second players defect

	int myScore = 0;
	int opp1Score = 0;
	int opp2Score = 0;

	int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
		// First Law: Always cooperate in first 2 rounds
		if (n < 2) return 0;

		// Second Law: Tolerate 2 consecutive defects from both opp
		// If 2 consecutive defects from both opp, then defect
		if (oppHistory1[n-1] == 1 && oppHistory1[n-2] == 1 &&
			oppHistory2[n-1] == 1 && oppHistory2[n-2] == 1)
			return 1;

		// Third Law: if one of the opponents is Nasty, then always defect
		boolean isOpp1Nasty, isOpp2Nasty;
		isOpp1Nasty = isNasty(n, oppHistory1);
		isOpp2Nasty = isNasty(n, oppHistory2);
		if (isOpp1Nasty || isOpp2Nasty) return 1;

		// Fourth Law: if one of the opponents is Random, then always defect
		boolean isOpp1Random, isOpp2Random;
		isOpp1Random = isRandom(n, oppHistory1);
		isOpp2Random = isRandom(n, oppHistory2);
		if (isOpp1Random || isOpp2Random) return 1;

		// Fifth Law: if my current score is lower than one of the opp, then always defect
		myScore += payoff[myHistory[n-1]][oppHistory1[n-1]][oppHistory2[n-1]];
		opp1Score += payoff[oppHistory1[n-1]][oppHistory2[n-1]][myHistory[n-1]];
		opp2Score += payoff[oppHistory2[n-1]][oppHistory1[n-1]][myHistory[n-1]];
		if (myScore < opp1Score || myScore < opp2Score) return 1;

		// Sixth Law: If above laws don't apply, then be a T4TPlayer
		if (Math.random() < 0.5) return oppHistory1[n-1];
		else return oppHistory2[n-1];
	}

	boolean isNasty(int n, int[] oppHistory) {
		int cnt = 0;
		for (int i=0; i<n; i++){
			if (oppHistory[i] == 1)
				cnt++;
		}

		if (cnt == n) return true;
		else return false;
	}

	boolean isRandom(int n, int[] oppHistory) {
		int sum = 0;
		double eps = 0.025;
		for (int i=0; i<n; i++) {
			sum += oppHistory[i];
		}

		// if ratio is roughly 0.5, then the opponent is highly likely to be random
		double ratio = (double) sum / n;
		if (Math.abs(ratio - 0.5) < eps) return true;
		else return false;
	}
}
