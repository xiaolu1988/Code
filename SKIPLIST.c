/* 

Example of Skip List source code for C:

Skip Lists are a probabilistic alternative to balanced trees, as
described in the June 1990 issue of CACM and were invented by 
William Pugh in 1987. 

This file contains source code to implement a dictionary using 
skip lists and a test driver to test the routines.

A couple of comments about this implementation:
  The routine randomLevel has been hard-coded to generate random
  levels using p=0.25. It can be easily changed.
  
  The insertion routine has been implemented so as to use the
  dirty hack described in the CACM paper: if a random level is
  generated that is more than the current maximum level, the
  current maximum level plus one is used instead.

  Levels start at zero and go up to MaxLevel (which is equal to
	(MaxNumberOfLevels-1).

The compile flag allowDuplicates determines whether or not duplicates
are allowed. If defined, duplicates are allowed and act in a FIFO manner.
If not defined, an insertion of a value already in the file updates the
previously existing binding.

BitsInRandom is defined to be the number of bits returned by a call to
random(). For most all machines with 32-bit integers, this is 31 bits
as currently set. 

The routines defined in this file are:

  init: defines NIL and initializes the random bit source 

  newList: returns a new, empty list 

  freeList(l): deallocates the list l (along with any elements in l)

  randomLevel: Returns a random level

  insert(l,key,value): inserts the binding (key,value) into l. If 
	allowDuplicates is undefined, returns true if key was newly 
	inserted into the list, false if key already existed

  delete(l,key): deletes any binding of key from the l. Returns
	false if key was not defined.

  search(l,key,&value): Searches for key in l and returns true if found.
	If found, the value associated with key is stored in the
	location pointed to by &value

*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// #define less(int a,int b) {return (a<b) ? 1 : 0}
#define less(a,b) (a < b)
#define equal(a,b) (!less(a,b)&&!less(b,a))

/* C语言中宏定义一个boolean类型. */
// 1. 
typedef int boolean;
#define True 1
#define False 0

#define NULLKey 500
// 2. 
typedef enum{false = 0,true = 1} BOOL;

// #define MaxLevels 10

// static struct link* head,nil;
// static int LEN;	// 跳表长度

typedef int KeyType;
typedef struct Item {
	KeyType key;
	int layers;
	// pass :other data members..
} Item;
#define NULLitem (Item item=NULL)

/* 1.跳表结点's存储结构 */
typedef struct link {
	// KeyType key;
	Item item;
	int level;
	struct link** nexts; // 2级指针指向 link*数组，实现动态类型

	/* Intermediate code: does not design properly/correctly. */
	// struct link* forward[1]; // -> 2级指针&trimToSize()方法 malloc\realloc\dealloc etc.
	// struct link* fwdlinks[];
} link; 

// typedef struct link* list; // 跳表结构

#define DefaultLevels 20
/* 2.跳表结构 */
typedef struct list {
	int length;
	int totlevels;	// total levels of list.
	// int defaultLevels; /* can be omitted by defining a global variable. */
	// struct link* headers[DefaultLevels]; // Oops:If not specify a fixed length, the execution will be terminated with a 'Bus error'.
	struct link* header;
	struct link* nil;	// tail node represents the end of the skip list. 
	// struct link* tail;

	/* auxiliary data structure. */
	// @Date: 2018.11.16 Friday 21:30
	struct link** tailsofeachlevel;
} list;

/* Function Declaration: */
struct list* newList();
void infor(list* l);
boolean isEmptyList(struct list* l);

/* 1. 构建一个empty skip list. */
struct list* newList() {
	/* initilize a nil node to represent the tail node of skip list. */
	link* nil;
	nil = (struct link*)malloc(sizeof(struct link));
	(*nil).item.key = NULLKey; //0x7fffffff;
	(*nil).level = 1;
	(*nil).nexts = NULL;

	struct list* l;
	l = (struct list*)malloc(sizeof(list));
	(*l).length = 0;
	(*l).totlevels = 1;
	l->nil = nil;

	struct link* h = malloc(sizeof(struct link));
	h->item.key = 0;
	h->level = 1;
	h->nexts = (struct link**)malloc(sizeof(struct link*));
	// *(h->nexts) = nil;
	*(h->nexts) = nil;

	if (*(h->nexts) == nil)
		printf("nexts is pointing to nil.\n");

	l->header = h;

	return l;
}

/* 2. isEmptyList()判断跳表是否为空 _Line11: */
boolean isEmptyList(struct list* l) {
	boolean is;
	is = *(l->header->nexts)==l->nil ? True : False;
	printf("%s,%d,is:%d\n","__isEmptyList()__:",112,is);
	// return (l->length == 0) ? True : False; //#1.根据长度判断length==0?True:False;
	// return l->header->nexts == &(*l).nil ? True : False; //& operator 居然不好使?F*ck!
	// return *(l->header->nexts) == l->nil ? True : False; //#2.根据结点的链接关系判断
	return is;
	// return False;
}

/* 3. 插入一个结点 */
/* 3.1_ 随机生成level */
int randomlevel() {
	return 3;
}

/* Build a new link node. */
link* linkCreation(struct list* l,Item item,int level) {
	struct link* node = (struct link*)malloc(sizeof(link));
	node->item = item;
	// printf("%s,%d\n","_linkCreation()_:134",item.key);
	node->level = randomlevel();

	node->nexts = (struct link**)calloc(level,sizeof(link*));
	int i = 0;
	for (;i < level; i++)
		node->nexts[i] = l->nil;

	return node;
}

/* 
 * insert a given item's key into skip list l at the specified level of m
 * @args: m represents the corrresponding level for a inserted node.
 */
/* Test code. */
boolean insertFirstLink(list* l,Item item,int m) {
	link* first = linkCreation(l,item,m);

	if (m > l->totlevels) {
		free(l->header->nexts);
		l->header->nexts = (struct link**)calloc(m,sizeof(link*));
	}
	l->header->level = m;
	l->length++;
	l->totlevels = m;
	// l->tail = node;

	int i = 0;
	for (;i < m; i++) 
		l->header->nexts[i]=first;

	/* Additional code: 生成指向每一层tail结点的指针数组 */
	// @Date:2018.11.16 21:31
	l->tailsofeachlevel=(struct link**)calloc(l->totlevels,sizeof(struct link*));
	i = 0;
	for (;i < m;i++) 
		l->tailsofeachlevel[i] = first; // 让first结点成为每一层level的tail结点

	return True; 
}

/* initial 思路,整体插入的思想 */
// boolean insertion01(struct list* l,Item item,int m);

/* @Date:2018.11.17(周6) 10:07 addition+. */
/* 将新结点插入到每一层level上. */
// refers to Line 47 list's structure:

// These code snipplets below is a big process/improvements of thoughts. #-》
boolean insertionOfLevel(struct list* l,link* new,int layer) {
	/* function declration here: */
	link* lookforPre(list* l,int insertKey,int m);
	void link2Nodes(link* pre,link* new,int m);

	link* pre = lookforPre(l,new->item.key,layer);
	link2Nodes(pre,new,layer);
	return True;
}

/* For each level,simulate the process of link 2 nodes. */ 
// 类似于单链表中对前后结点链接操作，只不过多了一个层次变量m,m为表示层数的索引 range:0,1,...
void link2Nodes(link* pre,link* new,int m) {
	(*new).nexts[m] = pre->nexts[m];
	(*pre).nexts[m] = new;
}

/* 找到每一层对应的插入位置，即pre结点的后面 */
link* lookforPre(list* l,int insertKey,int m) {
	link* first = l->header->nexts[m];
	link* pre,*cur;
	pre = l->header;
	cur = first;

	while(cur && cur!=l->nil && cur->item.key < insertKey) {
		pre = cur;
		cur = (*cur).nexts[m];
	}
	return pre;
}

/* insertion02 transition to s-> insertion */
// 1.General insertion:
boolean insertion(list* l,Item item,int m) {
	/* insert first node. */
	if (l->length == 0)
		return insertFirstLink(l,item,m);

	/* insert nodes that come after the first node. */

	/* find the correct position to be inserted. */
	// pre points to the node before the node will be inserted.

	// 插入结点到每一层level上
	struct link* new = linkCreation(l,item,m);

	// ensure. 能添加新的level,e.g. 原表层数为3，现插入一个level为5层的结点，则要保证第4、5层都被插入:
	if (m > l->totlevels) {
		struct link** newHeadNexts = (link**)calloc(m,sizeof(link*));
		int k = 0;
		for (;k < l->totlevels; k++)
			newHeadNexts[k] = l->header->nexts[k];

		free(l->header->nexts); //.删除掉原来header的forward指针集合
		l->header->nexts = newHeadNexts;
	}

	int layer=0; // :layer represents the total layers(' index) of the new inserted node.
	for (;layer < m;layer++)
		insertionOfLevel(l,new,layer);

	l->length++; //.插入后表长度+1
	if (m > l->totlevels) {
		l->totlevels = m;  //.出现新的level，更新表的总层数
		l->header->level = m;
	}

	return True;
}

/* 2.Optimized insertion: */
boolean insertion01(list* l,Item item,int m) {
	/* insert first node. */
	if (l->length == 0)
		return insertFirstLink(l,item,m);

	/* insert nodes that come after the first node. */

	/* find the correct position to be inserted. */
	// pre points to the node before the node will be inserted.

	// 插入结点到每一层level上
	struct link* new = linkCreation(l,item,m);

	// ensure. 能添加新的level,e.g. 原表层数为3，现插入一个level为5层的结点，则要保证第4、5层都被插入:
	if (m > l->totlevels) {
		/* 1. update header.nexts[] pointers array
		 */
		struct link** newHeadNexts = (link**)calloc(m,sizeof(link*));
		int k = 0;
		for (;k < l->totlevels; k++)
			newHeadNexts[k] = l->header->nexts[k];

		free(l->header->nexts); //.删除掉原来header的forward指针集合
		l->header->nexts = newHeadNexts;

		/* 2. update tailsofeachlevel
		*/
		struct link** tails = (struct link**)calloc(m,sizeof(link*));
		k=0;
		// for(;k < l->totlevels;k++) {
		// 	tails[k] = l->tailsofeachlevel[k];
		// }
		// tails[k] = new;
		for (;k<m;k++) { // replace the above logical error.
			if (k < l->totlevels)
				tails[k] = l->tailsofeachlevel[k];
			else 
				tails[k] = new;
		}
		free(l->tailsofeachlevel);
		l->tailsofeachlevel = tails; //.tails指针数组重新赋值
	}

	// concrete insertion operation:
	int layer=0; // :layer represents the total layers(' index) of the new inserted node.
	int insertKey = new->item.key;
	struct link* tail;
	for (;layer < m;layer++) {
		tail = l->tailsofeachlevel[layer];
		if (insertKey > tail->item.key) { //.大于tail,直接插入到tail的后面，并且成为新的tail
			link2Nodes(tail,new,layer); 
			l->tailsofeachlevel[layer] = new;
		}
		else //.不大于tail说明，需要到前面的结点去进行遍历，找到pre，然后插入
			insertionOfLevel(l,new,layer);
	}

	l->length++; //.插入后表长度+1
	if (m > l->totlevels) {
		l->totlevels = m;  //.出现新的level，更新表的总层数
		l->header->level = m;
	}
	return True;
}

/* 4.搜索结点 */
/* @args: m :the given level from which the searching started.
 */
// link* search(struct list* l,KeyType searchKey,int m) {
// 	return l->nil;
// }
//@addition time:2018.11.18(周日) 
boolean search(list* l,KeyType searchKey) {
	int m = (l->totlevels)-1;
	struct link* cur = l->header;
	struct link* nxt = cur;

	while(true) {
		nxt = cur->nexts[m];

		printf("while loop\n");
		printf("[cur,nxt] = [%d,%d]\n",cur->item.key,cur->item.key); //->nxt指针出现问题了!
		if (cur->nexts[m]==(l->nil)) {
			printf("%s: next is list's nil.\n","_LINE374_");
			// nxt = cur;
			m = m-1; 
			continue;
		}
		else {
			if (nxt->item.key == searchKey)	//case1:found.
				return True;
			else if (nxt->item.key < searchKey) {//case2:move forward. {}
				cur = nxt;	
			}
			else { //if (nxt->item.key > searchKey) {//case3:move down 
				m = m-1;
				if (m < 0) break;
			}			
		}
	}
	return False;
}

/* 5. 删除结点 */
boolean deletion(list* l,KeyType deletionKey) {
	// if not found {
		// pass: printf("Not found %d.",key);
		// return False;
	// } 
	// link* del = search(l,deletionKey); 
	/*
	if (!search(l,deletionKey)) {
		printf("Not found %d.\n",deletionKey);
		return False;
	}*/ /*
	int level = del->level;
	int n=0;
	link* pre;
	for(;n < level; n++) {
		pre = lookforPre(l,deletionKey,n);
		pre->nexts[n] = del->nexts[n];
		if (del==l->tailsofeachlevel[n])
			l->tailsofeachlevel[n] = pre;
		free(del);
	}*/
	return True;
}

/* 6. 遍历跳表 */
void traversal(list* l) {
	printf("%s,%d:\n","__ListTraversal__",286);

	if(isEmptyList(l)) 
		return;

	/* layer by layer traversal from up to bottom. */ 
	struct link* header = l->header;
	struct link* cur;

	int levels = l->totlevels;
	int n = levels-1;
	printf("\nlist's structure:\n");
	for (;n>=0;n--) { 		//逐层遍历: 输出对应层上结点的值
		printf("Level-%d: ",n+1);
		cur = header->nexts[n];
		while (cur && cur!=l->nil) {
			printf("%d ",cur->item.key);
			cur = cur->nexts[n];
		}
		printf("\n");
	}
	printf("\n");
}

/* 7.遍历所有层上的tail结点 */
void traversaloftails(list* l) {
	link** tails = l->tailsofeachlevel;
	int levels = l->totlevels;

	printf("tails of each layer:\n");
	int n=levels-1;
	for(;n>=0;n--) {
		printf("level-%d: ",n+1);
		printf("%d\n",tails[n]->item.key);
	}
	printf("\n");	
}

/* x. Print list information. */
void infor(list* l) {
	printf("Skip List:\n");
	printf("1.length = %d\n",(*l).length);
	printf("2.levels = %d\n",(*l).totlevels);
	// printf("3.header = %lu\n\n",sizeof((*l).header->forward)/sizeof(link*));
	printf("\n0.sizeof(link*)=%lu\n",sizeof(link*));
	printf("1.sizeof(int*)=%lu\n",sizeof(int*));
	printf("2.sizeof(int)=%lu\n\n",sizeof(int));
}

/* @Test part: */
// =============================================================================================================
void insertionTest(struct list* l) {
	int counts = 10;
	Item* items = (Item*)malloc(counts * sizeof(Item));
	int keys[] = {26,21,25,12,17,19,6,7,3,9};
	int levels[] = {1,1,3,1,2,1,4,1,1,2};

	counts = 0; 
	for (; counts < 10; counts++) {
		items[counts].key = keys[counts];
		items[counts].layers = levels[counts];

		// insertion(l,items[counts],items[counts].layers);
		insertion01(l,items[counts],items[counts].layers);
	} 
	traversal(l);
	traversaloftails(l);
}

void insertionTest01(list* l) {
	int counts = 3;
	Item* items = (Item*)malloc(counts*sizeof(Item));
	int keys[] = {26,25,6,30};
	int levels[] = {1,2,2,3};

	counts = 0;
	for(;counts < 3;counts++) {
		items[counts].key = keys[counts];
		items[counts].layers = levels[counts];

		insertion01(l,items[counts],items[counts].layers);
	}
	traversal(l);
	traversaloftails(l);

	printf("%s\n","_LINE511_:");
	int sk = 26;
	printf("search %d: %d\n\n",sk,search(l,sk));
}

void deletionTest(list* l) {
	deletion(l,25);
	traversal(l);
	traversaloftails(l);
}

void searchTest(list* l) {
	printf("search 1: %d\n",search(l,1));
	// printf("\nsearch 20: %d",search(l,20));
}
// ===============================================================================================================

int main(int argc,char* argv[]) {
	printf("(5 < 6 = %s)\n", less(5,6) ? "True" : "False");
	printf("(10 equals 10 = %s)\n", equal(10,10) ? "True" : "False");
	printf("%s %d,%d\n\n", "_LINE528_:",true,True);
	/* SKIPLIST ALGORITHMS: */
	// 1.insertion
	struct list* l = newList();
	// insertionTest(l);
	insertionTest01(l);
	deletionTest(l);
	// searchTest(l);
	return 1;
}
