package com.castorama.order;



import java.sql.Date;

//Referenced classes of package atg.commerce.csr:
//         FindOrdersFormHandler

public class DateRange
{

 Date getDateBoundary(Date date)
 {
     Date date1 = new Date(date.getYear() - getYearDiff(), date.getMonth() - getMonthDiff(), date.getDate() - getDayDiff());
     return date1;
 }

 public int getDayDiff()
 {
     return mDayDiff;
 }

 public String getDescription()
 {
     return mDescription;
 }

 public int getMonthDiff()
 {
     return mMonthDiff;
 }

 public int getYearDiff()
 {
     return mYearDiff;
 }

 public void setDayDiff(int i)
 {
     mDayDiff = i;
 }

 public void setDescription(String s)
 {
     mDescription = s;
 }

 public void setMonthDiff(int i)
 {
     mMonthDiff = i;
 }

 public void setYearDiff(int i)
 {
     mYearDiff = i;
 }

 int mYearDiff;
 int mMonthDiff;
 int mDayDiff;
 String mDescription;

 DateRange()
 {
     mYearDiff = 0;
     mMonthDiff = 0;
     mDayDiff = 0;
 }

 DateRange(int i, int j, int k, String s)
 {
     mYearDiff = 0;
     mMonthDiff = 0;
     mDayDiff = 0;
     setYearDiff(i);
     setMonthDiff(j);
     setDayDiff(k);
     setDescription(s);
 }
}