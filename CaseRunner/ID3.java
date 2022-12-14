package com.BigData.DT;
import java.util.ArrayList;

public class ID3
{
	public static void initialRecords(Records rds) {
		oneRecord record01 = new oneRecord(1);
		String[] infoArr01 = {"Summer","Sunny","no","CZ","no"};
		record01.setInfor(infoArr01);
		rds.addOneRecord(record01);
		
		oneRecord record02 = new oneRecord(2);
		String[] infoArr02 = {"Summer","Sunny","no","CA","no"};
		record02.setInfor(infoArr02);
		rds.addOneRecord(record02);

		oneRecord record03 = new oneRecord(3);
		String[] infoArr03 = {"Autumn","Sunny","no","CZ","yes"};
		record03.setInfor(infoArr03);
		rds.addOneRecord(record03);

		oneRecord record04 = new oneRecord(4);
		String[] infoArr04 = {"WinterSpring","RainyOrSnowy","no","CZ","yes"};
		record04.setInfor(infoArr04);
		rds.addOneRecord(record04);

		oneRecord record05 = new oneRecord(5);
		String[] infoArr05 = {"WinterSpring","Cloudy","yes","CZ","yes"};
		record05.setInfor(infoArr05);
		rds.addOneRecord(record05);

		oneRecord record06 = new oneRecord(6);
		String[] infoArr06 = {"WinterSpring","Cloudy","yes","CA","no"};
		record06.setInfor(infoArr06);
		rds.addOneRecord(record06);

		oneRecord record07 = new oneRecord(7);
		String[] infoArr07 = {"Autumn","Cloudy","yes","CA","yes"};
		record07.setInfor(infoArr07);
		rds.addOneRecord(record07);

		oneRecord record08 = new oneRecord(8);
		String[] infoArr08 = {"Summer","RainyOrSnowy","no","CZ","no"};
		record08.setInfor(infoArr08);
		rds.addOneRecord(record08);

		oneRecord record09 = new oneRecord(9);
		String[] infoArr09 = {"Summer","Cloudy","yes","CZ","yes"};
		record09.setInfor(infoArr09);
		rds.addOneRecord(record09);

		oneRecord record10 = new oneRecord(10);
		String[] infoArr10 = {"WinterSpring","RainyOrSnowy","yes","CZ","yes"};
		record10.setInfor(infoArr10);
		rds.addOneRecord(record10);

		oneRecord record11 = new oneRecord(11);
		String[] infoArr11 = {"Summer","RainyOrSnowy","yes","CA","yes"};
		record11.setInfor(infoArr11);
		rds.addOneRecord(record11);

		oneRecord record12 = new oneRecord(12);
		String[] infoArr12 = {"Autumn","RainyOrSnowy","no","CA","yes"};
		record12.setInfor(infoArr12);
		rds.addOneRecord(record12);

		oneRecord record13 = new oneRecord(13);
		String[] infoArr13 = {"Autumn","Sunny","yes","CZ","yes"};
		record13.setInfor(infoArr13);
		rds.addOneRecord(record13);

		oneRecord record14 = new oneRecord(14);
		String[] infoArr14 = {"WinterSpring","RainyOrSnowy","no","CA","no"};
		record14.setInfor(infoArr14);
		rds.addOneRecord(record14);
	}

	public static void main(String[] args) 
	{
		String[] strs = {"Season","Weather","A_control","Airline","DelayOrNot"};
		Records rds = new Records(strs);
		 
		// ????????? Records ???????????? ???????????????record??????????????? ?????????colSet???...
		initialRecords(rds);
		ArrayList<Integer> idxList = new ArrayList<Integer>();
		for (int i = 1; i <= 14;i++) 
			idxList.add(i);
		
		rds.setColSets(idxList);
		
		DecisionTree tree = new DecisionTree(rds);
		TreeNode root = new TreeNode();
		root.setParent(null);   // ???????????????parent
		tree.createDT(root);
		
		System.out.println("\n*************************************************************************************");
		System.out.println("????????????: ");
		// ????????????
		tree.preOrderTraverse(root);
		System.out.println("*************************************************************************************");
	}
}
        
// this class represents the whole data sets..
class Records
{
	private String[] properties;
	// first organize this formulation,change it later...
	private oneRecord[] recordsArr;
	private ArrayList<ArrayList<String>> columnSets;
	private boolean[] alreadyDecidedBool;
	// ????????????????????? ???????????? ?????? 
	private int idxForMaxGainCol;
	
	public Records() {
		
	}
	// ???recordsArr?????????????????? ??????colArr???
		public Records(String[] arr) {
			this.properties = arr;
			this.recordsArr = new oneRecord[14];
			// this.setColSets();
			
			// ????????? ???????????????????????? [ 0 0 0 0 ] ??????4?????? ??????season\weather\a_control\airline
			this.alreadyDecidedBool = new boolean[4];
			for (int i = 0; i < 4; i++)
				this.alreadyDecidedBool[i] = false;
		}
	
	public boolean containsPropName(String n) {
		for (String s:this.properties) {
			if (s.equals(n))
				return true;
		}
		return false;
	}
	
	// ??????????????????  all no/yes
	// ????????? leaf node : ?????? yes??????no; ???????????????????????? :??????	notALeaf
	public String getLeafNodeName() {
		String leafName = "";
		ArrayList<String> delayOrNotSubList = this.columnSets.get(4);
		String[] delays = (String[])delayOrNotSubList.toArray(new String[0]);
		
		// method 1: ?????? positive/negative ????????????entropy???0
		double entropy_forSubDelays = this.entropy(delays);
		if (entropy_forSubDelays == 0.0) 
			leafName = delayOrNotSubList.get(0);	
		else 
			leafName = "notALeaf";
		
		// method 2: if there exists errors in entropy method,analyze the linked list instead.
		
		//System.out.println("getLeafNodeName_: "+leafName);
		return leafName;
	} 
	
	public boolean[] getAlreadyDecidedBoolArr() {
		return this.alreadyDecidedBool;
	}
	
	public void setAlreadyDecidedBoolArr(boolean[] boolArr) {
		this.alreadyDecidedBool = boolArr;
	}
	
	// ?????? ?????? ???????????? ???properties?????????????????? 0:3 Season\weather\a_control\airline
	public int getIdxForMaxGainCol() {
		return this.idxForMaxGainCol;
	}
	
	public void setProperties(String[] propArr) {
		this.properties = propArr.clone();
	}

	// ?????? ?????? boolean ????????????
	public boolean[] getDecidedBoolArr() {
		return this.alreadyDecidedBool;
	}
	
	// ???????????? ?????? ????????????string list??????..
	public void setColSets(ArrayList<Integer> keysIdx) {
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		ArrayList<String> season = new ArrayList<String>();
		ArrayList<String> weather = new ArrayList<String>();
		ArrayList<String> a_control = new ArrayList<String>();
		ArrayList<String> airline = new ArrayList<String>();
		
		ArrayList<String> delayOrNot = new ArrayList<String>();
		
		for (int i = 0; i < keysIdx.size(); i++) {
			oneRecord r = recordsArr[keysIdx.get(i) - 1];
			
			season.add(r.getSeason());
			weather.add(r.getWeather());
			a_control.add(r.getAirCtrl());
			airline.add(r.getAirline());
			
			delayOrNot.add(r.getDelayOrNot());
		}
		
		list.add(season);
		list.add(weather);
		list.add(a_control);
		list.add(airline);
		
		list.add(delayOrNot);
		this.columnSets = list;
	}
	
	public ArrayList<ArrayList<String>> getColSets() {
		return this.columnSets;
	}

	public String[] getProperties() {    
		return this.properties;
	}

	// add one row data into the records
	public void addOneRecord(oneRecord oneR) {
		int id = oneR.getID();
		this.recordsArr[id - 1] = oneR;
	}

	// 
	public oneRecord[] getRecordSets() {
		return this.recordsArr;
	}

// ??????DelayOrNot ??????column??? entropy
	public double entropy(String[] strArr) {
		keywordLinkedList list = this.createLinkList(strArr);
		int[] a = list.keyCountArr();
		
		double entropy = 0.0;
		double p = 0.0;
		double logValue = 0.0;

		for (int j = 0; j < a.length; j++) {
			int n = a[j];
			p = (double)n/strArr.length;
			logValue = Math.log(p)/Math.log((double)2);

			entropy += (-1)*p*logValue;
		}
		return entropy;
	}

	// ????????????
	public keywordLinkedList createLinkList(String[] s) {
		keywordLinkedList list = new keywordLinkedList(new keywordNode(""));
		keywordNode node;
		
		for (int i = 0; i < s.length; i++) {
			String elem = s[i];
			if (list.containsKey(elem) == 0){
				node = new keywordNode(elem,1);
				list.insert(node, i+1);
			}
			
			else
				list.setKeysCountForNode(elem, i+1);
		}
		
		list.outputList();
		return list;
	} 
	
	// ?????? 	??????????????? 		information gain
	// ?????? ????????? ?????????????????? gain??????????????? this.columnSets???????????????????????????????????????????????????????????????
	public double gain(String colName) {
		double gain = 0.0;
		ArrayList<String> colStrList;
		ArrayList<String> delayStrList = this.columnSets.get(4);
		
		// ?????????Season?????? ???????????????info gain
		if (colName.equals("Season")) {
			colStrList = this.columnSets.get(0);
		}
		
		// weather???info gain
		else if (colName.equals("Weather")) {
			colStrList = this.columnSets.get(1);
		}
		
		else if (colName.equals("A_control")) {
			colStrList = this.columnSets.get(2);
		}
		
		// Airline?????????info gain
		else
			colStrList = this.columnSets.get(3);
	
		String[] s = (String[])colStrList.toArray(new String[0]);
		
		keywordLinkedList list = this.createLinkList(s);		
		ArrayList<ArrayList<Integer>> keyIdxList = list.getAllKeysIdx();
		
		ArrayList<String> _sameKeyStr = new ArrayList<String>();
		
		for (int ii = 0; ii < keyIdxList.size(); ii++) {
			ArrayList<Integer> li = keyIdxList.get(ii);
			int idxC = li.size();
			double proportion = (double)idxC /(double)colStrList.size();
	
			// ?????? 
			for (int jj = 0; jj < li.size(); jj++) {
				_sameKeyStr.add(delayStrList.get(li.get(jj)-1));
			}
			double subEntropy = this.entropy((String[])_sameKeyStr.toArray(new String[0]));
			gain += proportion * subEntropy;
		}
		return gain;
	}
	
	// ???????????? logical error..
	public boolean propHasDecided(String aProp) {
		String[] props = this.getProperties();
		for (int idx = 0; idx < props.length; idx++) {
			String s = props[idx];
			if (s.equals(aProp)) {
				int colIdx = this.getColumnIdx(s);
				return this.alreadyDecidedBool[colIdx];			
			}
		}	
		return false;
	}
	
	// ???????????? ?????????{season,weather,a_control,airline}????????????
	public int getColumnIdx(String aProp) {
		String[] props = this.getProperties();
		for (int i = 0; i < props.length; i++) {
			String elem = props[i];
			if (elem.equals(aProp))
				return i;
		}
		return -1;
	}
	
	// logical error... 
	// ????????????????????? ????????? ??????
	public String findDecisionNodeName() {
		// ???????????? : ?????? yes or no 
		String isLeafName = this.getLeafNodeName();
		if (!isLeafName.equals("notALeaf"))
			return isLeafName;
		
		// ??? ????????????: ?????? info gain ????????????????????? ?????? : 
		double[] gainsArr = new double[this.properties.length - 1];
		for (int m = 0; m < this.properties.length - 1; m++) {
			String aProp = this.properties[m];
			double gValue;
			// ???????????????????????? ??? ????????????gain?????????????????????????????????????????????
			if (this.propHasDecided(aProp))
				gValue = 100.f;
			else 
				gValue = this.gain(aProp);
			gainsArr[m] = gValue;
		}
		this.idxForMaxGainCol = (short)this.simpleSort(gainsArr);
//		this.alreadyDecidedBool[this.idxForMaxGainCol] = true;
		return this.properties[this.idxForMaxGainCol];
	}
	
	/*
	public String findRootDecisionNode() {
		double[] gainsArr = new double[this.properties.length];
		
		for (int m = 0; m < this.properties.length; m++) {
			String aProp = this.properties[m];
			double gValue = this.gain(aProp);  // ??????each column????????????
			gainsArr[m] = gValue;
		}
		
		this.idxForMaxGainCol = (short)this.simpleSort(gainsArr);
		
		// ?????? ?????? info gain??? ??????????????????????????????
		this.alreadyDecidedBool[this.idxForMaxGainCol] = true;
		return this.properties[this.idxForMaxGainCol];
	}
	*/
	
	/*
	public TreeNode createRootNode() {
		// ?????? ?????? ?????????????????? ????????? :) 01
		String rootName = this.findRootDecisionNode();
		TreeNode treeRoot = new TreeNode(rootName);
		
		// ?????? ????????? ??? ????????? ?????? ?????? [ CA CZ ] or [ no yes ] :) 03
		int idxForNode = this.getIdxForMaxGainCol();
		String[] propStrs = (String[])this.columnSets.get(idxForNode).toArray(new String[0]);
		keywordLinkedList list = this.createLinkList(propStrs);
		String[] allKeysArr = list.getAllKeysArr();
	     
		// test branchnames
		treeRoot.setBranchNames(allKeysArr);
		
		for (String s:treeRoot.getBranchNames()) 
			System.out.print("branchesName:"+s+" ");  
		
		// :)4?????? keyIdx
		treeRoot.setDistinctKeyIdx(list.getAllKeysIdx());
		// test keyIdx
		for (ArrayList<Integer> numbList:treeRoot.getDistinctKeyIdx()) {
			for (int i:numbList) 
				System.out.print(i+" ");
			System.out.println();
		}
		return treeRoot;
	}
	*/
	
	// ?????????????????? ?????????????????????????????????????????????????????????????????????????????? ??????
	public int simpleSort(double[] arr) {
		int min;
		int minIdxInOrigArr = 0;
		
		for (int i = 0; i < arr.length-1; i++) {
			min = i;
			for (int j = i+1; j < arr.length; j++) {
				if (arr[j] < arr[min])
					min = j;
			}
			if (i == 0)
				minIdxInOrigArr = min;
			
			if (min != i) {
				double temp = arr[i];
				arr[i] = arr[min];
				arr[min] = temp;
			}
		}
		return minIdxInOrigArr;
	} 
}

class oneRecord
{	
	private int recordID; 
	private String Season;
	private String Weather;
	private String A_control;
	private String Airline;
	private String DelayOrNot;
	
	public int getID() {
		return this.recordID;
	}
	// set ID for each column so as to identify it clearly....
	public oneRecord(int ID) {
		this.recordID = ID;
	}

	public void setInfor(String[] arr) {
		this.Season = arr[0];
		this.Weather = arr[1];
		this.A_control = arr[2];
		this.Airline = arr[3];
		this.DelayOrNot = arr[4];
	}

	public String getSeason() {
		return this.Season;
	}

	public String getWeather() {
		return this.Weather;
	}

	public String getAirCtrl() {
		return this.A_control;
	}
	public String getAirline() {
		return this.Airline;
	}

	public String getDelayOrNot() {
		return this.DelayOrNot;
	}
}

// ????????? ?????????
class keywordNode
{
	public String key;
	public int keyCount;
	public short idx;
	public ArrayList<Integer> indexArr;
	public keywordNode next;
	
	public keywordNode(String key) {
		this.key = key;
		this.next = null;
		
		this.indexArr = new ArrayList<Integer>();
	}

	public keywordNode(String key,int keyC) {
		this.key = key;
		this.next = null;
		this.keyCount = keyC;
		
		this.indexArr = new ArrayList<Integer>();
	}
}

// ??????????????? ???????????? ????????????????????????????????? ???????????????
class keywordLinkedList
{
	// ?????????
	private keywordNode headNode;
	private int[] individualKeyCountArr;
	private ArrayList<ArrayList<Integer>> allKeysIdxArr;
	
	// ??????????????????keys ??????     :)1
	private ArrayList<String> allKeysArr;

	// ?????? ?????? CZ CA??????????????????????????????String?????????	:)1
	public String[] getAllKeysArr() {
		return (String[])this.allKeysArr.toArray(new String[0]);
	}

	public ArrayList<ArrayList<Integer>> getAllKeysIdx() {
		return this.allKeysIdxArr;
	}
	
	public keywordLinkedList(keywordNode head) {
		this.headNode = head;
		this.allKeysIdxArr = new ArrayList<ArrayList<Integer>>();
		this.allKeysArr = new ArrayList<String>();
	}
	
	// insert node 
	public void insert(keywordNode newNode,int idx) {
		keywordNode current = this.headNode;
		while(current.next != null) {
			current = current.next;
		}
		current.next = newNode;
		current.next.indexArr.add(idx);
	}

	// get length
	public int length() {
		int length = 0;
		if (this.headNode.next == null)
			return 0;
		
		keywordNode nextNode = this.headNode;
		while(nextNode.next!=null) {
			length++;
			nextNode = nextNode.next;
		}
		return length;
	}

	public int containsKey(String key) {
		keywordNode nextNode = this.headNode;
		
		String keyword;
		while(nextNode.next != null) {
			nextNode = nextNode.next;
			keyword = nextNode.key;

			if (keyword.equals(key))
			{
				return 1;
			}
		}
		return 0;
	}

// ???????????????keyNode ???????????? ????????? keyCount
	public void setKeysCountForNode (String key,int keyIdx) {
		keywordNode keyNode = this.headNode;
		String keyWord;

		while(keyNode.next != null) {
			keyNode = keyNode.next;
			keyWord = keyNode.key;

			if (!keyWord.equals(key))
				continue;
			else {
				keyNode.keyCount++;
				keyNode.indexArr.add(keyIdx);
			}
		}
	}
	
// ???????????????????????????????????? ??????count???
	public int keysCount(String key) {
		keywordNode node = this.headNode;
		String keyWord;

		int count = 0;
		while(node.next != null) {
			node = node.next;
			keyWord = node.key;

			if (!keyWord.equals(key))
				continue;
			else {
				count = node.keyCount;
				return count;
			}
		}
		return count;
	}

	// ?????? list
	public void outputList() {
		//System.out.println("\nlist length: "+this.length());
		keywordNode node = this.headNode;
		int[] a = new int[this.length()];
		
		int i = 0;
		while(node.next != null) {
			node = node.next;
			this.allKeysIdxArr.add(node.indexArr);
			this.allKeysArr.add(node.key);
			
			// ?????? ??????????????????????????? 1.?????????+2.?????????occurence times+3.?????????????????????

			/**
			 *  ??????????????? ??????key ??????????????? : name+times+indexArr[]
			 */
			
			/*
			System.out.print(node.key+" "+node.keyCount+" ??????Arr:");			
			for (int idx = 0; idx < node.indexArr.size(); idx++) {
				if (idx == 0) 
					System.out.print("[ ");
				System.out.print(node.indexArr.get(idx)+" ");
				if (idx == node.indexArr.size()-1)
					System.out.print("]");
			}	
			System.out.println();
			*/
			
			a[i++] = node.keyCount;
		}
		this.individualKeyCountArr = a;
	}

	// ?????? ????????????key ???????????????
	public int[] keyCountArr() {
		return this.individualKeyCountArr;
	}
}

// TreeNode ?????????
class TreeNode {
	private String nodeName; // ????????? : ??????????????????
	private String[] branchNames; // ??????????????????????????? ?????????
	private ArrayList<ArrayList<Integer>> distinctKeyIdxList; // :)
	private TreeNode parent;
	private int whichChild;  	// ??????????????????????????????
	private ArrayList<TreeNode> nodeChilds; // ???????????????????????????????????? ?????? ????????????

	public void setWhichChild(int idx) {
		this.whichChild = idx;
	}
	
	public int getWhichChild() {
		return this.whichChild;
	}
	
	public void setParent(TreeNode pa) {
		this.parent = pa;
	}

	public TreeNode getParent() {
		return this.parent;
	}

	public TreeNode() {
		this.nodeChilds = new ArrayList<TreeNode>();
		this.distinctKeyIdxList = new ArrayList<ArrayList<Integer>>();
	}

	public TreeNode(String na) {
		this.nodeName = na;
		this.nodeChilds = new ArrayList<TreeNode>();
		this.distinctKeyIdxList = new ArrayList<ArrayList<Integer>>(); // :)
	}

	public void setNodeName(String n) {
		this.nodeName = n;
	}

	// :)
	public void setDistinctKeyIdxList(ArrayList<ArrayList<Integer>> idx) {
		this.distinctKeyIdxList = idx;
	}

	public ArrayList<ArrayList<Integer>> getDistinctKeyIdxList() {
		return this.distinctKeyIdxList;
	}

	public String getNodeName() {
		return this.nodeName;
	}

	// branchNames setter-getter
	public void setBranchNames(String[] names) {
		this.branchNames = names;
	}

	public String[] getBranchNames() {
		return this.branchNames;
	}

	// childs setter-getter methods..
	public ArrayList<TreeNode> getNodeChilds() {
		return this.nodeChilds;
	}

	public void setNodeChilds(ArrayList<TreeNode> childs) {
		this.nodeChilds = childs;
	}
}

class DecisionTree {
	// ??????????????? ????????? dt
	public Records dataSets;
	public DecisionTree(Records records) {
		this.dataSets = records;
		this.whichDTPath = 1;
	}
	
	public Records getRecords() {
		return this.dataSets;
	}
	private int whichDTPath;
	
	/**
	 * 	??? ?????????
	 * 	function: ????????????TreeNode????????????????????????..
	 * @param treeNode
	 */
	public void createDT(TreeNode treeNode) {
		String nodeName = this.dataSets.findDecisionNodeName();
		//System.out.println("\nnode_name: "+nodeName);
		
		/* equavilent... */
		// rds.containsPropName(nodeName) == false <=> ??????????????? nodeName=yes|no
		if (!nodeName.equals("notALeaf") && this.dataSets.containsPropName(nodeName) == false) {
			//System.out.println("\n???????????? :"+nodeName);
			treeNode.setNodeName(nodeName);    // 1.
			treeNode.setBranchNames(null);
			treeNode.setDistinctKeyIdxList(null);
			treeNode.setNodeChilds(null);   // ?????????????????? ????????????  // 5.
			return;
		}	
		
		// 1.nodeName ??????
		treeNode.setNodeName(nodeName);
		// ?????????????????? ????????????
		TreeNode current = treeNode;
		
		boolean[] decisionBoolArr = this.dataSets.getAlreadyDecidedBoolArr();
		for (int i = 0; i < decisionBoolArr.length; i++) 
			decisionBoolArr[i] = false;
		
		while(current != null) {
			int idxForProp = this.dataSets.getColumnIdx(current.getNodeName());
			System.out.print("????????????: "+current.getNodeName()+" "+idxForProp);
			decisionBoolArr[idxForProp] = true;
			
			this.dataSets.setAlreadyDecidedBoolArr(decisionBoolArr);
			current = current.getParent();	// ???????????????????????????????????????????????????
		}
		
		System.out.print("\n???????????? bool :");
		for (boolean b:this.dataSets.getAlreadyDecidedBoolArr())
			System.out.print(b+" ");
		
		// 2.?????? ??? 
		int max_ig_col_idx = this.dataSets.getIdxForMaxGainCol(); // 0 1 2 3 ??????season???weather..
		String[] propsStrs = (String[])this.dataSets.getColSets().get(max_ig_col_idx).
				toArray(new String[0]);
		if (nodeName.equals("Weather")) {
			for (String s:propsStrs) 
				System.out.print("weather :"+s+" ");
			
			System.out.println("weather:colsets:length: "+this.dataSets.getColSets().get(max_ig_col_idx).size());
		}
		keywordLinkedList list = this.dataSets.createLinkList(propsStrs);
		String[] allKeysArr = list.getAllKeysArr();  // {CA,CZ}
		
		if (nodeName.equals("Weather")) {
			System.out.println("allKeysArr-length: "+allKeysArr.length);
			for (String s:allKeysArr)
				System.out.print(s+" ");
		}

		treeNode.setBranchNames(allKeysArr);
		
		System.out.print("\n"+nodeName+" ??????: ");
		for (String s:allKeysArr) 
			System.out.print(s+" ");
		
		// 3.???????????? ??????????????? ?????? ??? 
		treeNode.setDistinctKeyIdxList(list.getAllKeysIdx());  // ArrayList<ArrayList<Integer>>
		ArrayList<ArrayList<Integer>> _idxList = treeNode.getDistinctKeyIdxList();
		
		System.out.println("\n????????????: ");
		for (ArrayList<Integer> numb:_idxList) {
			for (int i:numb)
				System.out.print(i+" ");
			System.out.println();
		}
		
		// 4.??????childs
		ArrayList<TreeNode> __childs;	  // ???????????? list
		ArrayList<Integer> idxArr;			// ??????????????? ????????? ??????
		
		for (int i = 0; i < allKeysArr.length;i++) {
			idxArr = _idxList.get(i); 				// cz???ca ???????????????
			this.dataSets.setColSets(idxArr);			// ??????sub?????????

			__childs = treeNode.getNodeChilds();
			TreeNode aChild = new TreeNode();
			// 4. ?????? ?????????
			aChild.setParent(treeNode);
			aChild.setWhichChild(i);
			// 5.?????? ????????????
			__childs.add(aChild);
			
			// ?????? ??????
			createDT(aChild);
		}
	}
	
	// ?????? ?????? ?????????
	/**
	 * 
	 * @param treeNode
	 */
	public void preOrderTraverse(TreeNode root) {
		ArrayList<TreeNode> childs = root.getNodeChilds();
		TreeNode current = root;
		TreeNode child = root;
		// ?????? ????????????  

		if (childs == null)  { 
			// ????????? ?????????
			ArrayList<String> dtStr = new ArrayList<String>();
			dtStr.add("=>THEN delayOrNot="+root.getNodeName());
			
			while(current != null) {
				current = current.getParent();
				
				if (current != null) {
					String name = current.getNodeName();
					String branchName="";
					int whichChild = child.getWhichChild();
				
					branchName = current.getBranchNames()[whichChild];
					
					String s = name+"="+branchName+" ";
					dtStr.add(s);
				}
				
				child = child.getParent();
			} 
			
			// ???????????? 
			System.out.print((this.whichDTPath++)+") IF ");
			int dtStrL = dtStr.size();
			for (int i = dtStrL - 1; i >= 0; i--) {
				if (i != dtStrL - 1 && i != 0)
					System.out.print("AND "+dtStr.get(i));
				else 
					System.out.print(dtStr.get(i));
			}

			System.out.println();
			return;
		}
		
	// 	System.out.print(root.getNodeName()+" ");
		for (TreeNode node:childs)
			preOrderTraverse(node);
	}
}


