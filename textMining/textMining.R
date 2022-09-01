# Warning 1: custom the following variables in your defined name. Don't be the same with given to you.
# 璇存槑:鍙橀噺鏇挎崲锛屾敼涓嬭繖鍑犱釜鍙橀噺鐨勫弬鏁?
# 
yourStuID <- "104754150827"
yourCustomDirName <- "wyDir"
outFiles <- "requiredFiles"

# remove(list = ls())
# rm(list = ls())

# Part one: random picks 1000 documents ..            ===
# load the needed library "tm" & "SnowballC"
#--------------------------------------Part one:--------------------------------------
    # install the needed packages 
#  install.packages(c("tm","SnowBallC","wordcloud","RColorBrewer"))

# if (!installed.packages(c("tm","SnowBallC","wordcloud","RColorBrewer")))
#     install.packages(c("tm","SnowBallC","wordcloud","RColorBrewer"))

# 鍏堟斁鑷繁寤虹珛鐨勭洰褰曚笅锛宨s not writable浠ュ悗瑙ｅ喅
# create a new directory
#--------------------------------------Part one:--------------------------------------
newADir <- function (dirPath) {
  if (!dir.exists(dirPath))
    dir.create(dirPath)
}

r_pkgPth <- paste(getwd(),"/rPackages",sep = "")
newADir(r_pkgPth)

.libPaths(new = r_pkgPth)

tm_pkg_path <- paste(r_pkgPth,"/tm",sep = "")
if (!dir.exists(tm_pkg_path)) {
    install.packages("tm",lib = r_pkgPth)
    # library("tm",lib.loc = r_pkgPth)
    library("tm")
}

snow_pkg_path <- paste(r_pkgPth,"/SnowballC",sep = "")
if (!dir.exists(snow_pkg_path)) {
  install.packages("SnowballC",lib = snow_pkg_path)
  # library("SnowballC",lib.loc = snow_pkg_path)
  library("SnowballC")
}

wordcloud_pkg_path <- paste(r_pkgPth,"/wordcloud",sep = "")
if (!dir.exists(wordcloud_pkg_path)) {
  install.packages("wordcloud",lib = r_pkgPth)
  # library("wordcloud",lib.loc = r_pkgPth)
  library("wordcloud")
}

rColorBrewer_pkg_path <- paste(r_pkgPth,"/RColorBrewer",sep = "")
if (!dir.exists(rColorBrewer_pkg_path)) {
  install.packages("RColorBrewer",lib = r_pkgPth)
  # library("RColorBrewer",lib.loc = r_pkgPth)
  library("RColorBrewer")
}

# packageList <- path.package()
#pkgName <- NULL

#for (ii in 1:length(packageList)) {
#    onePackagePath <- packageList[ii]
#    sep <- strsplit(onePackagePath,split = "/")[[1]]
#    pkgName <- c(pkgName,sep[length(sep)])
#}

#if (!any(pkgName == "tm")) {
#    install.packages("tm")
#    library("tm",lib.loc = .libPaths())
#}

#if (!any(pkgName == "SnowballC")) {
#  install.packages("SnowballC")
#  library("SnowballC",lib.loc = .libPaths())
#}

#if (!any(pkgName == "wordcloud")) {
#    install.packages("wordcloud")
#    library("wordcloud",lib.loc = .libPaths())
#}


#if (!any(pkgName == "RColorBrewer")) {
#    install.packages("RColorBrewer")
#    library("RColorBrewer",lib.loc = .libPaths())
#}

#--------------------------------------Part one:--------------------------------------
  # load the needed package into memory...

# library("tm")
# library("SnowballC")
# library("wordcloud")
# library("RColorBrewer")



# random picks X = 400,Y = 600,T = "sci.space" documents from the 20newsgroup-18828 document
docsDir <- paste(getwd(),"/",yourCustomDirName,sep = "")
# print("docsDir:")
# print(docsDir)

newADir(docsDir)

newADir(paste(getwd(),"/",outFiles,sep = ""))

scispacepath <- "20news-18828/sci.space"
sciFiles <- list.files(path = scispacepath)
sciFileCount <- length(sciFiles)

# produce 400 random numbers from x (1:982)
random400Numbers <- function(x) {
    len <- length(x)
    nbsVec <- NULL
    nbsVec <- sample(seq(1,len,by=1),400,replace = F)
    return (nbsVec)
}

randomPos <- random400Numbers(sciFiles)   # random produce 400 numbers from 1 to the length of files that sci.space contains.
xFileNames <- NULL                        # the vector used to save the 400 random produced Files names 

dir_400 <- paste(yourCustomDirName,"/400",sep = "")
if (dir.exists(dir_400))
  unlink(dir_400,recursive = TRUE)

newADir(dir_400)

dir_1000 <- paste(yourCustomDirName,"/1000",sep = "")
if (dir.exists(dir_1000))
  unlink(dir_1000,recursive = TRUE)

newADir(dir_1000)

## randomly picks 400 unique documents that belong to topic "sci.space"
## also means procude 400 random nummbers from 1:982(the length of vector sciFiles)
for (i in 1:400) {
  index <- randomPos[i]
  xFileName <- sciFiles[index]
  from <- paste("20news-18828/sci.space/",xFileName,sep = "")
  #to <- paste("docsDir/txt",i,sep = "")
  
  to <- dir_400
  to01 <- dir_1000
  #   print(from)
  #  print(to)
  
  #file.copy(from,to,overwrite = TRUE,copy.mode = TRUE)
  file.copy(from,to01,overwrite = TRUE,copy.mode = TRUE)  #copy 400 files to the 1000 documents..

  fromIdx <- paste(yourCustomDirName,"/1000/",xFileName,sep = "")
  toIdx <- paste(yourCustomDirName,"/1000/",subNameFor600Files(from),sep = "")
  
  file.rename(fromIdx,toIdx)

# print(xFileName)
# print(paste("sci.space/",xFileName,sep = ""))
  
  xFileNames <- c(xFileNames,sciFiles[index])
}
# 鏁版嵁闆嗘湁閲嶅悎鐨刦ile锛屾病鏈塩opy600涓枃浠躲€傘€?
print("400 file copy completed..")

traverseOtherDir <- function() {
  news20Dir <- "20news-18828"
  dirList <- dir(news20Dir)
  
  dirList <- dirList[-15]
  #print(dirList)
  
  total_files_len <- 0
  total_files_name_vector <- NULL
  
  for (i in 1:length(dirList)) {
    oneDir <- dirList[i]
    
    path <- paste("20news-18828/",oneDir,sep = "")
    
    oneDir_fileList <- list.files(path)
    nameVector <- NULL
    
    for (j in 1:length(oneDir_fileList)) {
      item <- paste(path,"/",oneDir_fileList[j],sep = "")
      #print(item)
      nameVector <- c(nameVector,item)
    }
    
    oneDir_filesCount <- length(oneDir_fileList)
    total_files_len <- total_files_len + oneDir_filesCount
    
    total_files_name_vector <- c(total_files_name_vector,nameVector)
  }
  
  return(total_files_name_vector)
}

theOtherFiles <- traverseOtherDir()
theOtherLen <- length(theOtherFiles)


print("鍏朵粬鐩綍涓嬬殑鏂囦欢鍚嶏細")
print(theOtherFiles)

# find 600 files in the other file directory and replace the name....
subNameFor600Files <- function(x) {
    charCount <- nchar("20news-18828/")
    
    startIdx <- charCount + 1
    stopIdx <- nchar(x)
    
    subString <- substr(x,startIdx,stopIdx)
    subString <- gsub(pattern = "/",replacement = "_",subString)
    return (subString)
}

# print(paste("other topics file count :",theOtherLen,sep = ""))

randomVector <- NULL
randomVector <- sample(seq(1,theOtherLen,by=1),600,replace = FALSE)

dir_600 <- paste(yourCustomDirName,"/600",sep = "")

if (dir.exists(dir_600))
  unlink(dir_600,recursive = T)

newADir(dir_600)

for (j in 1:600) {
  index <- randomVector[j]
  fileName <- theOtherFiles[index]
  
  from <- fileName
  to <- dir_600
  
  to01 <- dir_1000

  file.copy(from,to01,overwrite = T,copy.mode = TRUE)   #copy 600 files to the 1000 documents... 

  #  file.rename(from = fileName,to = paste(getwd(),yourCustomDirName,"/1000")subNameFor600Files(fileName))
  
  docID <- strsplit(from,split = "/")[[1]][3]
  fromIdx <- paste(yourCustomDirName,"/1000/",docID,sep = "")
  toIdx <- paste(yourCustomDirName,"/1000/",subNameFor600Files(fileName),sep = "")
  file.rename(fromIdx,toIdx)
}

#print("600 files copy completed.")
#print("1000 files copy completed.")
# print(paste("1000 file length :",length(list.files(path = "docsDir/1000"))))

# picks other 600 files from the remaining directory except the dir:sci.space...
#--------------------------------------Part one END--------------------------------------


# Step Two: create the corpus
#----------------------------------Part Two:------------------------------------------
dirPth <- paste(getwd(),paste("/",yourCustomDirName,"/1000",sep = ""),sep = "")

#print(dirPth)
x <- DirSource(directory = dirPth,encoding = "UTF-8")
cid <- VCorpus(x,readerControl = list(reader = x$DefaultReader,language = "en"))

# Step Three: text transformation / preprocessing
#----------------------------------Part Three:------------------------------------------
stopwordsList <- c("a","about","above","after","again","against","all","am","an","and","any",
                   "are","aren't","as","at","be","because","been","before","being","below","between",
                   "both","but","by","can't","cannot","could","did","didn't","do","does",
                   "doesn't","doing","don't","down","during","each","few","for","from","further","had",
                   "hadn't","has","hasn't","have","haven't","having","he","he'd","he'll","he's","her",
                   "here","here's","hers","herself","him","himself","his","how","how's","i","i'd",
                   "i'll","i'm","i've","if","in","into","is","isn't","it","it's","its",
                   "itself","let's","me","more","most","mustn't","my","myself","no","nor","not",
                   "of","off","on","once","only","or","other","ought","our","ours","ourselves",
                   "out","over","own","same","shan't","she","she'd","she'll","she's","should","shouldn't",
                   "so","some","such","than","that","that's","the","their","theirs","them","themselves",
                   "then","there","there's","these","they","they'd","they'll","they're","they've","this","those",
                   "through","to","too","under","until","up","very","was","wasn't","we","we'd",
                   "we'll","we're","we've","were","weren't","what","what's","when","when's","where","where's",
                   "which","while","who","who's","whom","why","why's","with","won't","would","wouldn't",
                   "you","you'd","you'll","you're","you've","your","yours","yourself","yourselves")

#-----------------------------------------------------------------------------------------
#-------------------------------------text transformation---------------------------------
cid <- tm_map(cid,PlainTextDocument)
cid <- tm_map(cid,content_transformer(tolower))
cid <- tm_map(cid,removeWords,stopwordsList)

toSpace <- content_transformer(function(x, pattern) gsub(pattern, " ",x))
cid <- tm_map(cid,toSpace,"[[:punct:]]")    # remove the punctuation
cid <- tm_map(cid,toSpace,"[[:blank:]]")    # remove the blank character
cid <- tm_map(cid,toSpace,"[[:digit:]]")    # remove the numbers 0-9

cid <- tm_map(cid,stemDocument)

# The File name format is  < sci.space_60392 >
oneKFilesNames <- list.files(path = paste(getwd(),paste("/",yourCustomDirName,"/1000",sep = ""),sep = ""))

## eliminate in the end <>removed afterwards<>
##  writeCorpus(cid,path = paste(getwd(),"/writeCorpus",sep = ""),filenames = files)

#-----------------------------------------------------------------------------------------
#-------------------------------------create the matrix-----------------------------------
tdm <- TermDocumentMatrix(cid)
#as.matrix(tdm)
tdm <- weightTfIdf(tdm,normalize = FALSE)

matrixName <- function() {
      n <- paste(yourStuID,"_matrix.txt",sep = "")
      
  if (yourStuID == "104753150678")
      n <- "Lu_matrix.txt"
    
    return (n)
}

filePath <- paste(getwd(),"/",outFiles,"/",matrixName(),sep = "")
if(!file.exists(filePath))
    file.create(filePath)

# files <- list.files(path = paste(getwd(),"/docsDir/1000",sep = ""))
colnames(tdm) <- oneKFilesNames

data <- as.data.frame(inspect(tdm[,]))
write.table(data,file = filePath)
#-----------------------------------------------------------------------------------------


# Step Four: process the query....
#-----------------------------------------------------------------------------------------

# first,get the query,and cut it into items....
queryVector <- scan(file = paste(getwd(),"/Query/Query.txt",sep = ""),what = "")
# print(queryVector)

# get the matrix's row names,namely through this,we can get the index terms dictionary.
indexTermsDict <- rownames(tdm)
# print(length(indexTermsDict))
#print(indexTermsDict)

# construct a VectorSouce volatile corpus
#y <- VectorSource(queryVector)
#print(queryVector)
#queryCorpus <- VCorpus(y,readerControl = list(reader = y$DefaultReader,language = "en"))

#queryCorpus <- tm_map(queryCorpus,PlainTextDocument)
#queryCorpus <- tm_map(queryCorpus,removeWords,stopwordsList)
#queryCorpus <- tm_map(queryCorpus,stemDocument)

#----------------------------------------------------------------------------------------------------------------------------
# process the query vector...
# queryCorpusPth <- paste(getwd(),"/txtFiles",sep = "")

x <- DirSource(directory = paste(getwd(),"/Query",sep = ""),encoding = "UTF-8")
qCorpus <- VCorpus(x,readerControl = list(reader = x$DefaultReader,language = "en"))

qCorpus <- tm_map(qCorpus,PlainTextDocument)
qCorpus <- tm_map(qCorpus,removeWords,stopwordsList)
qCorpus <- tm_map(qCorpus,stemDocument)
qCorpus <- tm_map(qCorpus,stripWhitespace)

# <>remove afterwards<>
#writeCorpus(qCorpus,path = queryCorpusPth,filenames = NULL)

tdm01 <- TermDocumentMatrix(qCorpus)
#tdm01 <- weightTfIdf(tdm01,normalize = TRUE)  # this sentense does not have meaning here...

tdm01 <- weightTf(tdm01)

tdmData <- as.data.frame(inspect(tdm01[,]))
q_matrix_path <- paste(getwd(),"/query_matrix.txt",sep = "")
if (!file.exists(q_matrix_path))
    file.create(q_matrix_path)

# put the query matrix to the local disk file...  two columns Matrix,key names & Tf weights
write.table(tdmData,file = q_matrix_path)

#--------------------------------------------------------------------------------------------------------------------------------
# verify if the terms dictionary contains all the terms in the q vector
# the method to judge one vector contains the other vector..    --  Improvement point:  --

# be careful of this method's execution time .For this can be a very large execution time ..... 12.7
judgeTwoVectors <- function(mainV,subV) {
    foundElems <- NULL
    for (i in 1:length(subV)) {
        subElem <- subV[i]
        for (j in 1:length(mainV)) {
            mainElem <- mainV[j]
            if (mainElem == subElem)
              foundElems <- c(foundElems,subElem)
        }
    }
    
    return (foundElems)
}
 
# construct the query vector Tf-Idf weight.
query_vector_afterProcessing <- rownames(tdm01)
#foundElems <- judgeTwoVectors(indexTermsDict,query_vector_afterProcessing)
#print(foundElems)

queryIDF <- NULL
# caculate the df(i) in the collections:

tdm02 <- TermDocumentMatrix(cid)
tdm02 <- weightTfIdf(tdm02)

keywordMatrix <- as.matrix(weightTfIdf(TermDocumentMatrix(cid,list(dictionary = query_vector_afterProcessing))))
colnames(keywordMatrix) <- oneKFilesNames
  
# print("The keyword matrix is :")
# print(keywordMatrix)

#---------------------------------------------------------------------------------------------------------------------------
# pick one column from the keyword matrix

#------------------------------------------------------------------------------------------------------
## caculate the similarity between the q vector and the d vector 
q <- c(1,1,1,1,1,1,1,1,1,1)   # fully filled with 1 as default.    improvement aferwards...

# non-relevant document id 
nonRelevantDocs <- NULL
nonRelevantDocs_numb <- 0

cosineQandD <- function (oneColumn,columnName) {
  
  numerator <- NULL
  denominator <- NULL
  
  # the similarity value...
  sim <- NULL
  
  #---------------------------------------------------------------------------------------------------------
  # judge if all elements in the vector equal to 0
  allElemsEqual0 <- all(oneColumn == 0)
  if (allElemsEqual0 == TRUE) {
    print(columnName)
    print("all 0")
    
    nonRelevantDocs <<- c(nonRelevantDocs,columnName)     # write the global variable. 
    nonRelevantDocs_numb <<- nonRelevantDocs_numb + 1
    
    print("Non-relevant documents Number:")
    print(nonRelevantDocs_numb)
    sim <- 0
    return (sim)
  }

  numerator <- crossprod(x = oneColumn,y = q)  
  denominator <- sqrt(crossprod(oneColumn,oneColumn)) * sqrt(crossprod(q,q))
  
  sim <- round(x = (numerator / denominator),digits = 6)
  
  return (sim)
}

# define a vector to store each documents ranking score....
scoreVec <- NULL # each element of it is a list that has 2 sub-elements: the score & the document ID
namesVec <- NULL     # put the column name as the element's name for storage.

for (k in 1:length(oneKFilesNames)) {
    columnName <- oneKFilesNames[k]
    
    oneColumn <- keywordMatrix[,columnName]
    # <---------------------------------------------------> #
      # use for debugging... 
     # print(paste("The ",k," column:"))
    #  print(oneColumn)
    # <---------------------------------------------------> #
     
    score <- 0
    score <- cosineQandD(oneColumn,columnName)
   
    if (score == 0) 
        next
    
    # there exists a issue,that the score/columnName maybe NULL,which will cause an strange issue..
    #if (!is.null(score) && !is.null(columnName)) {
    #  scElem <- list(SCORE = score,DOCUMENTID = columnName)
    #  scoreVec <- c(scoreVec,scElem)
    #}
    
    # assign the columnName(also is the document id) for each element's name of the vector 
    scoreVec <- c(scoreVec,score)
    
    namesVec <- c(namesVec,columnName)
    names(scoreVec) <- namesVec
    
}

# <-----------------------------------------------------------------------------------------------> #
# use for debugging... 

#    print("score vector: ")
#    print(scoreVec)
#    print("score vector length:")
#    print(length(scoreVec))

#    print("non-relevant vector:")
#    print(nonRelevantID)
#    print("non-relevant vector length:")
#    print(length(nonRelevantID))
    
    # Test the structure of the score vector..
#    scoreVec[1]
#    scoreVec[2]
#    scoreVec[3]
    
#    length(scoreVec)
    
#    print(nonRelevantDocs_numb)
#    print(nonRelevantDocs)
# <-----------------------------------------------------------------------------------------------> #

#--------------------------------------------------------------------------------------------------------------------------------------
# sort the score vector 
scoreVec <<- sort(scoreVec,decreasing = TRUE)
 #print(scoreVec)
    
#--------------------------------------------------------------------------------------------------------------------------------------
# construct the sid_results.txt file
# issues: 1. row names :character not so good.
#         2. ranking score .6 precision.
#         3. process the category component.
#         optimize the above issues.    

    resultsDF <- NULL
  #  idComponent <- as.numericnames(scoreVec)
    # component one:
 
#---------------------------------------------------------------------------------------------------------------------------------------------------------    
 # write the result.txt file algorithm start..
  
    # seprate the file name into 2 different parts:
    id_vector <- NULL
    category_vector <- NULL
    
    names_vector <- names(scoreVec)
    for (m in 1:length(names_vector)) {
        name_forOneElem <- names_vector[m]
        
        string_afterSpliting <- strsplit(name_forOneElem,split = "_")
        
        id_vector <- as.numeric(c(id_vector,string_afterSpliting[[1]][2])) 
        category_vector <- c(category_vector,string_afterSpliting[[1]][1])
    }
    
  #  idComponent <- as.numeric(names(scoreVec))
  #   print(idComponent)
    
    # component two:
 #   categoryComponent <- NULL
    # component three:
    rankingScComponent <- scoreVec
 #   print(rankingScComponent)
    
    resultsDF <- data.frame(documentID = id_vector,category = category_vector,rankingScore = rankingScComponent,stringsAsFactors = FALSE)
    
    # row.names(resultsDF) <- as.nc(1:length(scoreVec))
    row.names(resultsDF) <- c(1:length(scoreVec))       #as.numeric()
#    print(resultsDF)
    
    
    dfWriteToPth <- paste(getwd(),"/",outFiles,"/Lu_results.txt",sep = "")
    if (yourStuID != "104753150678")
        dfWriteToPth <- paste(getwd(),"/",outFiles,"/",yourStuID,"_results.txt",sep = "")
    
    if (!file.exists(dfWriteToPth))
          file.create(dfWriteToPth)
    
    # write the result file to the local disk...
    write.table(resultsDF,file = dfWriteToPth,row.names = TRUE)
    # write the result.txt file algorithm Ended.
#--------------------------------------------------------------------------------------------------------------------------
    
#--------------------------------------------------------------------------------------------------------------------------
  # produce the wordcloud png 
    x <- DirSource(directory = dirPth,encoding = "UTF-8")
    m_corpus <- VCorpus(x,readerControl = list(reader = x$DefaultReader,language = "en"))
    m_corpus <- tm_map(m_corpus,PlainTextDocument)
    m_corpus <- tm_map(m_corpus,content_transformer(tolower))
    m_corpus <- tm_map(m_corpus,removeWords,stopwordsList)
    
    toSpace <- content_transformer(function(x, pattern) gsub(pattern, " ",x))
    m_corpus <- tm_map(m_corpus,toSpace,"[[:punct:]]")    # remove the punctuation
    m_corpus <- tm_map(m_corpus,toSpace,"[[:blank:]]")    # remove the blank character
    m_corpus <- tm_map(m_corpus,toSpace,"[[:digit:]]")    # remove the numbers 0-9
    
    m_matrix <- TermDocumentMatrix(m_corpus)
    m_matrix <- as.matrix(m_matrix)
    colnames(m_matrix) <- oneKFilesNames
    
    m_matrix <- m_matrix[,names(scoreVec)]
#    m_matrix
#    jpgFileName <- function(x) {
#       if (x != "104753150678")
#           return (paste(yourStuID,"_cloud.png",sep = ""))
        
#        return ("Lu_cloud.png")
#   }
    
#    jpgFileName(yourStuID)
    
    jpgFileName <- paste(outFiles,"/Lu_cloud.jpg",sep = "")
    if (yourStuID != "104753150678") {
        jpgFileName <- paste(outFiles,"/",yourStuID,"_cloud.jpg",sep = "")
    } 
  
    png(jpgFileName,width = 1000,height = 800)              
    
    v <- sort(rowSums(m_matrix),decreasing = TRUE)
    d <- data.frame(word = names(v),freq = v)
    pal2 <- brewer.pal(8,"Dark2")
    wordcloud(d$word,d$freq,scale = c(10,1),
                        max.words = 50,
                        random.order = FALSE,
                        random.color = F,
                        ordered.colors = F,
                        rot.per = .15,
                        colors = pal2)
          dev.off()
#    colnames(m_matrix) <- NULL
    
#    comparison.cloud(m_matrix,
#                     max.words = 1000,
#                     scale = c(5,0.5),
#                     random.order = FALSE,
                     
#                     rot.per = 0.1,
                #     colors = brewer.pal(8,"Dark2"),
#                     random.color = F,
#                     ordered.colors = F,
#                     colors = c("red","orange","yellow","green","cyan","blue","purple"),
#                     title.size = 1.4)
    
#    dev.off()
#--------------------------------------------------------------------------------------------------------------------------

#--------------------------------------------------------------------------------------------------------------------------
# retrieval evaluation:   
# procedures:
          
#         1. random picks 10 relevant documents from the 400 files random picked from the sci.space fileholder.
              # (This will be the relevant document. [R] for representation)
#         2. For the answer document sets.namely the files from the sid_results.txt,compare if there exists 10 files
            # that appears in the R sets. and caculate the '11 standard recall levels'
#         3. using the plot function to plot the recall versus precison curve 
#         4. caculate the F1 measure for the 10 percent recall level.   F1 = 2*p*r / (p + r)
#         5. save the graph file sid_eval.f1.jpg to the disk..
          
#--------------------------------------------------------------------------------------------------------------------------
# 1.define the global variables that maybe used for the forthcoming.
RDocsID <- NULL
print(xFileNames)
random10DocsID <- sample(seq(1,400,by=1),10,replace = FALSE)

for (ii in 1:10)
    RDocsID <- c(RDocsID,xFileNames[random10DocsID[ii]])

# print(RCollections)

AnswerSets <- NULL
AnswerSets <- id_vector    # get the id_vector(docoumentID) component as the answer sets...

# 2.judge how many elems that occurs in the R collection that appears in the answersets.
findMatchedElems <- function (R,A) {
    matchedElemsCount <- 0
    
    R_len <- length(R)
    A_len <- length(A)
    
    for (ii in 1:A_len) {
        elemsInA <- A[ii]
        
        for (jj in 1:R_len) {
            if (R[jj] == elemsInA) 
              matchedElemsCount <- matchedElemsCount + 1
        }
    }
    
    return (matchedElemsCount)
}

# counts <- findMatchedElems(c(1,2,3,4,5,6,7,8,9,10),c(2,3,4))    # test the function

# find out how many elements appear in R occurs in A
matchedElemsCount <- findMatchedElems(RDocsID,AnswerSets)
tempVector <- RDocsID    # a temp vector to save the element in R. and when matches a element,remove it,so 
                   # the length of the document reduce 1,and will get a more fast executing speed which will enhance the efficency.
print(tempVector)

precisionVector <- NULL   # a vector to save the precision of 11 standard level values from (0.0 0.1 0.2 .... 1.0)
recallCount <- 0  # statistic how many recall level that have returned....

precision <- NULL
theNextLoopIndex <- 1    # store the index while we needn't  to loop for all the elements.

#while (recallCount < matchedElemsCount) {
   # for (ii in 1:length(id_vector)) {
  #      aAnswerElem <- id_vector[ii]
   #     if (any(RDocsID == aAnswerElem)) # find a matched elements. namely recall a relevant-document.
  #      {
  #          recallCount <- recallCount + 1
   #     }  
  #  }
  #  recallCount <- recallCount + 1
#}

for (ii in 1:length(id_vector)) {
    aAnswerElem <- id_vector[ii]
    if (any(RDocsID == aAnswerElem)) {  # find a matched elements. namely recall a relevant-document.
        recallCount <- recallCount + 1
        
        precision <- c(precision, recallCount / ii)
    }
    
    if (recallCount == matchedElemsCount) 
      break
}

p_len <- length(precision)
print("Precision length:")
print(p_len)

if (p_len < 10) {
    offNumbs <- 10 - p_len
    x <- rep(precision[p_len],times = offNumbs)
    precision <- c(precision[1],precision[1:p_len],x)   # re-construct the precision vector... 
                                                        # that represent the 11 standard recall levels
}

P <- precision[2]
R <- 0.1

f1_measure <- 2*P*R/(P+R)     # caculate the F1 measure..

print("Precision:")
print(precision)

#--------------------------------------------------------------------------------------------------------------------------------
# save the plot to local disk .... 

plotFileName <- paste(getwd(),"/",outFiles,"/Lu_eval_",round(f1_measure,2),".jpg",sep = "")
if (yourStuID != "104753150678")
  plotFileName <- paste(getwd(),"/",outFiles,"/",yourStuID,"_eval_",round(f1_measure,2),".jpg",sep = "")

jpeg(filename = plotFileName,width = 800,height = 600,units = "px",quality = 75,res = NA)
# plot the recall versus precision curve graph..
yValues <- c(precision) * 100
g_range <- range(0,yValues)

max_y <- ceiling(max(yValues)) 

plot(yValues,type = 'o',col = 'black',ylim = g_range,axes = FALSE,ann = FALSE)
axis(1,at = 1:11,lab = c(0,10,20,30,40,50,60,70,80,90,100))
axis(2,las = 1,at = ceiling((max_y / 5))*(0:max_y))     # has issues..

box()

lines(yValues,type = 'o',pch = 19,lty = 20,col = "black")

title(main = "Recall/Precision Curve",col.main = 'red',font.main = 4)
title(xlab = "Recall",col.lab = rgb(1,0,0),font.main = 5)
title(ylab = "Precision",col.lab = rgb(1,0,0),font.main = 5)

dev.off()
#--------------------------------------------------------------------------------------------------------------------------------
#----------------------------------------------END-------------------------------------------------
