# SQL Quries

###### Table Setup (DDL)
This section of the documentation provides the SQL Data Definition Language (DDL) statements used to set up the necessary tables for the club database. These tables are crucial for managing members, bookings, and facilities efficiently.

#### Creation of Tables

The following subsections describe the creation of each table, including their fields and the relationships between them.

##### 1\. Members Table

Contains detailed records about each member of the club.

```sql
CREATE TABLE IF NOT EXISTS cd.members (
memid SERIAL PRIMARY KEY,
surname VARCHAR(200) NOT NULL,
firstname VARCHAR(200) NOT NULL,
address VARCHAR(300) NOT NULL,
zipcode INTEGER NOT NULL,
telephone VARCHAR(20) NOT NULL,
recommendedby INTEGER REFERENCES cd.members(memid),
joindate TIMESTAMP NOT NULL
);
```

##### 2\. Bookings Table

Records all bookings made for the club's facilities.

```sql
CREATE TABLE IF NOT EXISTS cd.bookings (
bookid SERIAL PRIMARY KEY,
facid INTEGER NOT NULL REFERENCES cd.facilities(facid),
memid INTEGER NOT NULL REFERENCES cd.members(memid),
starttime TIMESTAMP NOT NULL,
slots INTEGER NOT NULL
);
```

##### 3\. Facilities Table

Details each facility available at the club, including costs and maintenance information.

```sql
CREATE TABLE IF NOT EXISTS cd.facilities (
facid SERIAL PRIMARY KEY,
name VARCHAR(100) NOT NULL,
membercost NUMERIC NOT NULL,
guestcost NUMERIC NOT NULL,
initialoutlay NUMERIC NOT NULL,
monthlymaintenance NUMERIC NOT NULL
);
```

###### Question 1: Adding a new facility

```sql
insert into cd.facilities
    values (9, 'Spa', 20, 30, 100000, 800);   
```

###### Question 3: Adding a Facility with Auto-Generated ID

```sql
insert into cd.facilities select (select max(facid) from cd.facilities)+1, 'Spa', 20, 30, 100000, 800; 
```

###### Question 3: Updating Data in a Row

```sql
update cd.facilities
set initialoutlay = 10000
where facid = 1;
```

###### Question 4: Updating Data with Calculations

```sql
update cd.facilities facs
    set
        membercost = facs2.membercost * 1.1,
        guestcost = facs2.guestcost * 1.1
    from (select * from cd.facilities where facid = 0) facs2
    where facs.facid = 1;
```

###### Question 5: Deleting All Rows

```sql
delete from cd.bookings;   
```

###### Question 6: Deleting Rows with a Condition

```sql
delete from cd.members where memid=37;
```

###### Question 7: Selecting with a Filter (Fee < 1/50th of Monthly Maintenance)

```sql
select facid, name, membercost, monthlymaintenance 
	from cd.facilities 
	where 
		membercost > 0 and 
		(membercost < monthlymaintenance/50.0); 
```

###### Question 8: Filtering with LIKE Pattern Matching

```sql
select * from cd.facilities where name Like '%Tennis%';
```

###### Question 9: Filtering with OR Condition

```sql
select * 
from cd.facilities
where facid = 1 or facid = 5;
```


###### Question 10: Filtering with Date Condition

```sql
select memid,surname,firstname,joindate
from cd.members 
where joindate >= '2012-09-01';
```


###### Question 11: Using UNION to Combine Queries

```sql
select surname 
	from cd.members
union
select name
	from cd.facilities;
```

###### Question 12: Inner Join with Filter

```sql
select bks.starttime 
	from cd.bookings bks
		inner join cd.members mems on mems.memid = bks.memid
	where mems.firstname='David' and mems.surname='Farrell';  
```
###### Question 13: Inner Join with Multiple Filters

```sql
select bk.starttime as start, fac.name as name
	from cd.facilities fac
		inner join cd.bookings bk on fac.facid = bk.facid
	where 
		fac.name in ('Tennis Court 2','Tennis Court 1') and
		bk.starttime >= '2012-09-21' and
		bk.starttime < '2012-09-22'
order by bk.starttime;        
```

###### Question 14: Left Outer Join

```sql
select mem.firstname as memfname, mem.surname as memsname, rec.firstname as recfname, rec.surname as refsname
    from cd.members mem
        left outer join cd.members rec on rec.memid = mem.recommendedby
order by memsname, memfname;
```

###### Question 15: Show All Members with Distinct Recommenders

```sql
select distinct recs.firstname as firstname, recs.surname as surname
	from 
		cd.members mems
		inner join cd.members rec on recs.memid = mems.recommendedby
order by surname, firstname; 
```


###### Question 16: Show All Members with Their Recommenders

```sql
select distinct mems.firstname || ' ' ||  mems.surname as member,
	(select recs.firstname || ' ' || recs.surname as recommender 
		from cd.members recs 
		where recs.memid = mems.recommendedby
	)
	from 
		cd.members mems
order by member;          
```


###### Question 17: Group by & Order by

```sql
select recommendedby, count(*)
from cd.members
where recommendedby is not null
group by recommendedby
order by recommendedby;
```

###### Question 18: Group By and Order By with Aggregation

```sql
select facid,sum(slots) as "Total Slots"
from cd.bookings
group by facid
order by facid;
```
###### Question 19: Group By with Condition

```sql
select facid, sum(slots) as "Total Slots"
from cd.bookings
where starttime >= '2012-09-01' and starttime < '2012-10-01'
group by facid
order by sum(slots);          
```

###### Question 19: Group By, Order By, and Extract Function

```sql
select facid, extract(month from starttime) as month, sum(slots) as "Total Slots"
from cd.bookings
where extract ( year from starttime) = 2012
group by facid, month
order by facid, month;
```

###### Question 20: Distinct Count

```sql
select count(distinct memid) from cd.bookings;
```

###### Question 21: Inner Join with Group By and Aggregation

```sql
select mems.surname,mems.firstname,mems.memid, min(bks.starttime) as starttime
from cd.bookings bks
inner join cd.members mems on 
mems.memid = bks.memid
where starttime >= '2012-09-01'
group by mems.surname, mems.firstname, mems.memid
order by mems.memid;
```

###### Question 22: Window fucntion

```sql
select count(*) over(), firstname, surname
	from cd.members
order by joindate  
```


###### Question 23: Window Function with Row Number

```sql
select row_number() over(order by joindate), firstname, surname
	from cd.members
order by joindate          
```

###### Question 24: Window Function with Subquery and Group By

```sql
select facid, total from (
	select facid, sum(slots) total, rank() over (order by sum(slots) desc) rank
        	from cd.bookings
		group by facid
	) as ranked
	where rank = 1   
```


###### Question 25: Formatting Strings

```sql
select surname || ', ' || firstname as name from cd.members   
```


###### Question 24: Formatting Strings with Filter

```sql
select memid, telephone from cd.members where telephone ~ '[()]';     
```


###### Question 24: Window Function with Subquery and Group By

```sql
select substr (mems.surname,1,1) as letter, count(*) as count 
from cd.members mems
group by letter
order by letter   
```

