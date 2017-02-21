#COMAP Challenge Problem D dataset
setwd("~/Desktop/")

#This reads in the provided data observations
df = read.csv("2017_ICM_Problem_D_Data.csv", header = TRUE, stringsAsFactors = FALSE)

#This function converts the given times into seconds
stringToSeconds = function(timeString)
{
  splitstr = strsplit(timeString, '[:.]')
  mins = splitstr[[1]][1]
  secs = splitstr[[1]][2]
  milsecs = splitstr[[1]][3]
  
  mins = as.numeric(mins)
  secs = as.numeric(secs)
  milsecs = as.numeric(milsecs)

  seconds = 60*mins + secs + milsecs/10
  return(seconds)
}

#This function converts Time.to.get.scanned.property into seconds
stringToSeconds2 = function(timeString)
{
  splitstr = strsplit(timeString, '[:.]')
  mins = splitstr[[1]][1]
  secs = splitstr[[1]][2]
  
  mins = as.numeric(mins)
  secs = as.numeric(secs)
  
  seconds = 60*mins + secs
  return(seconds)
}

for(i in 1:8)
{
  for( j in 1:length(df[,i]))
  {
    if(i == 8)
    { df[j,i] = stringToSeconds2(df[j,i]) }
    else
    { df[j,i] = stringToSeconds(df[j,i]) }
  }
  
  df[,i] = as.numeric(df[,i])
}

df$TSA.Pre.Check.Arrival.Intervals = NA
df$Regular.Pax.Arrival.Intervals = NA
df$Milimeter.Wave.Scan.Intervals = NA
df$X.Ray.Scan.Intervals = NA
df$X.Ray.Scan.1.Intervals = NA



#Compute the time intervals between customers for columns 1,2,5,6,7
i=1
while (i<58)
{
  if(!is.na(df$TSA.Pre.Check.Arrival.Times[i+1]))
  {df$TSA.Pre.Check.Arrival.Intervals[i] = df$TSA.Pre.Check.Arrival.Times[i+1] - df$TSA.Pre.Check.Arrival.Times[i]}
  else
  {df$TSA.Pre.Check.Arrival.Intervals[i] = NA}
  
  if(!is.na(df$Regular.Pax.Arrival.Times[i+1]))
  {df$Regular.Pax.Arrival.Intervals[i] = df$Regular.Pax.Arrival.Times[i+1] - df$Regular.Pax.Arrival.Times[i]}
  else
  {df$Regular.Pax.Arrival.Intervals[i] = NA}
  
  if(!is.na(df$Milimeter.Wave.Scan.times[i+1]))
  {df$Milimeter.Wave.Scan.Intervals[i] = df$Milimeter.Wave.Scan.times[i+1] - df$Milimeter.Wave.Scan.times[i]}
  else
  {df$Milimeter.Wave.Scan.Intervals[i] = NA}
  
  if(!is.na(df$X.Ray.Scan.Time[i+1]))
  {df$X.Ray.Scan.Intervals[i] = df$X.Ray.Scan.Time[i+1] - df$X.Ray.Scan.Time[i]}
  else
  {df$X.Ray.Scan.Intervals[i] = NA}
  
  if(!is.na(df$X.Ray.Scan.Time.1[i+1]))
  {df$X.Ray.Scan.1.Intervals[i] = df$X.Ray.Scan.Time.1[i+1] - df$X.Ray.Scan.Time.1[i]}
  else
  {df$X.Ray.Scan.1.Intervals[i] = NA}
  
  i=i+1;
}


#These lines of code combine the small samples for two agents ID check process time into one ID check process column
df$ID.Check.Process.Intervals.All = paste(df$ID.Check.Process.Time.1)
df$ID.Check.Process.Intervals.All[10:16] <- paste(df$ID.Check.Process.Time.2[1:7])
df$ID.Check.Process.Intervals.All = as.numeric(df$ID.Check.Process.Intervals.All)

#Same for two Xray scan time columns
df$X.Ray.Scan.All.Intervals <- paste(df$X.Ray.Scan.Intervals)
df$X.Ray.Scan.All.Intervals[11:13] <- paste(df$X.Ray.Scan.1.Intervals[1:3])
df$X.Ray.Scan.All.Intervals = as.numeric(df$X.Ray.Scan.All.Intervals)

frame = df[,c(9,10,14,11,15,8)]


#Export CSV to print the table
write.table(frame, "Data_intervals_in_seconds.csv", sep = ",", na = "", col.names = TRUE, row.names = FALSE)

#Create a data frame to hold sample statistics for each field of the data
frame.stats.rownames = c("sample.mean", "sample.std.dev", "sample.variance", "sample.median", "min", "max")
frame.stats = data.frame(row.names = frame.stats.rownames)
frame.stats[colnames(frame)] = NA

for(i in 1:6)
{
  frame.stats[1,i] = mean(frame[,i], na.rm = TRUE)
  frame.stats[2,i] = sd(frame[,i], na.rm = TRUE)
  frame.stats[3,i] = var(frame[,i], na.rm = TRUE)
  frame.stats[4,i] = median(frame[,i], na.rm = TRUE)
  frame.stats[5,i] = min(frame[,i], na.rm = TRUE)
  frame.stats[6,i] = max(frame[,i], na.rm = TRUE)
}


# #Export CSV to print the table
write.table(frame.stats, "Data_stats.csv", sep = ",", na = "", col.names = TRUE, row.names = TRUE)



#Exploratory Data Analysis
#Start making plots of probability distributions

qqnorm(frame$TSA.Pre.Check.Arrival.Intervals)
qqline(frame$TSA.Pre.Check.Arrival.Intervals)
#not normal

qqnorm(frame$Regular.Pax.Arrival.Intervals)
qqline(frame$Regular.Pax.Arrival.Intervals)
#not normal

qqnorm(frame$ID.Check.Process.Intervals.All)
qqline(frame$ID.Check.Process.Intervals.All)
#Possibly normal, run further analysis

qqnorm(frame$Milimeter.Wave.Scan.Intervals)
qqline(frame$Milimeter.Wave.Scan.Intervals)
#not normal

qqnorm(frame$X.Ray.Scan.All.Intervals)
qqline(frame$X.Ray.Scan.All.Intervals)
#not normal

qqnorm(frame$Time.to.get.scanned.property)
qqline(frame$Time.to.get.scanned.property)
#Possibly normal, run further analysis


install.packages("fitdistrplus")
library(fitdistrplus)



fitdistr(frame[1:57,1], "exponential")
ks.test(frame[1:57,1], pexp, 0.10882016)

fitdistr(frame[1:46,2], "exponential")
ks.test(frame[1:46,2], pexp, 0.07724601)

fitdistr(frame[1:16,3], "exponential")
ks.test(frame[1:16,3], pexp, 0.08918618)
plot(fitdist(frame[1:16,3], pexp, method = "mle"))


fitdistr(frame[1:39,4], "exponential")
ks.test(frame[1:39,4], pexp, 0.08594094)
plot(fitdist(frame[1:39,4], pexp, method = "mle"))


fitdistr(frame[1:13,5], "exponential")
ks.test(frame[1:13,5], pexp, 0.15046296)

fitdistr(frame[1:29,6], "exponential")
ks.test(frame[1:29,6], pexp, 0.03493976)
plot(fitdist(frame[1:29,6], pexp, method = "mle"))


#Matches for 1,3,4,6 :)







fitdistr(frame[1:57,1], "logistic")
ks.test(frame[1:57,1], plogis, 7.827327, 5.398111)
plot(fitdist(frame[1:57,1], plogis, method = "mle"))

fitdistr(frame[1:46,2], "logistic")
ks.test(frame[1:46,2], plogis, 10.1550421, 7.0691493)
plot(fitdist(frame[1:46,2], plogis, method = "mle"))


fitdistr(frame[1:16,3], "logistic")
ks.test(frame[1:16,3], plogis, 10.9572973, 2.0649751)

fitdistr(frame[1:39,4], "logistic")
ks.test(frame[1:39,4], plogis, 10.7438818, 2.4471902)

fitdistr(frame[1:13,5], "logistic")
ks.test(frame[1:13,5], plogis, 5.2933547, 3.6739597)

fitdistr(frame[1:29,6], "logistic")
ks.test(frame[1:29,6], plogis, 27.750124, 7.627534)







fitdistr(frame[1:57,1], "cauchy")
ks.test(frame[1:57,1], pcauchy, 3.2065194, 3.5228113)
plot(fitdist(frame[1:57,1], pcauchy, method = "mle"))

fitdistr(frame[1:46,2], "cauchy")
ks.test(frame[1:46,2], pcauchy, 7.232191, 5.920937)
plot(fitdist(frame[1:46,2], pcauchy, method = "mle"))

fitdistr(frame[1:16,3], "cauchy")
ks.test(frame[1:16,3], pcauchy, 10.7062223, 2.1711404)

fitdistr(frame[1:39,4], "cauchy")
ks.test(frame[1:39,4], pcauchy, 10.4735999, 1.9522644)

fitdistr(frame[1:13,5], "cauchy")
ks.test(frame[1:13,5], pcauchy, 2.0221832, 0.7101327)

fitdistr(frame[1:29,6], "cauchy")
ks.test(frame[1:29,6], pcauchy, 26.284715, 6.401838)









fitdistr(frame[1:57,1], "gamma")
ks.test(frame[1:57,1], pgamma, 0.85256553, 0.09277640)

fitdistr(frame[1:46,2], "gamma")
ks.test(frame[1:46,2], pgamma, 0.86828092, 0.06706917)

fitdistr(frame[1:16,3], "gamma")
ks.test(frame[1:16,3], pgamma, 9.5917762, 0.8554539)

fitdistr(frame[1:39,4], "gamma")
ks.test(frame[1:39,4], pgamma, 5.9287822, 0.5095249)

fitdistr(frame[1:13,5], "gamma")
ks.test(frame[1:13,5], pgamma, 1.15521166, 0.17381394)

fitdistr(frame[1:29,6], "gamma")
ks.test(frame[1:29,6], pgamma, 3.69865446, 0.12923019)






fitdistr(frame[1:57,1], "weibull")
ks.test(frame[1:57,1], pweibull, 0.89182297, 8.68082388)

fitdistr(frame[1:46,2], "weibull")
ks.test(frame[1:46,2], pweibull, 0.8865157, 12.1369500)

fitdistr(frame[1:16,3], "weibull")
ks.test(frame[1:16,3], pweibull, 3.2200529, 12.5086915)

fitdistr(frame[1:39,4], "weibull")
ks.test(frame[1:39,4], pweibull, 2.100549, 13.163830)

fitdistr(frame[1:13,5], "weibull")
ks.test(frame[1:13,5], pweibull, 1.0208909, 6.7122080)

fitdistr(frame[1:29,6], "weibull")
ks.test(frame[1:29,6], pweibull, 2.1726823, 32.3055878)







fitdist(frame[1:57,1], pt, method = "mle", start = list(df = 56))
ks.test(frame[1:57,1], pt, df = 0.4752003)
plot(fitdist(frame[1:57,1], pt, method = "mle", start = list(df = 56)))

fitdist(frame[1:46,2], pt, method = "mle", start = list(df = 45))
ks.test(frame[1:46,2], pt, df = 0.4081581)
plot(fitdist(frame[1:46,2], pt, method = "mle", start = list(df = 45)))

fitdist(frame[1:16,3], pt, method = "mle", start = list(df = 15))
ks.test(frame[1:16,3], pt, df = 0.3457965)
plot(fitdist(frame[1:16,3], pt, method = "mle", start = list(df = 15)))

fitdist(frame[1:39,4], pt, method = "mle", start = list(df = 38))
ks.test(frame[1:39,4], pt, df = 0.3451542)
plot(fitdist(frame[1:39,4], pt, method = "mle", start = list(df = 38)))

fitdist(frame[1:13,5], pt, method = "mle", start = list(df = 12))
ks.test(frame[1:13,5], pt, df = 0.5728939)
plot(fitdist(frame[1:13,5], pt, method = "mle", start = list(df = 12)))

fitdist(frame[1:29,6], pt, method = "mle", start = list(df = 28))
ks.test(frame[1:29,6], pt, df = 0.2544471)
plot(fitdist(frame[1:29,6], pt, method = "mle", start = list(df = 28)))




install.packages("visualize")
library("visualize", lib.loc="~/Library/R/3.3/library")
visualize.cauchy(stat=0, location = 3.2055, scale = 3.5213)
visualize.cauchy(stat=0, location = 7.237, scale = 5.919)
visualize.exp(stat=1, theta = 0.08918)

