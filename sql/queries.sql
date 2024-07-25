-- Data Manipulation Queries (DML)

-- Adding a New Facility
INSERT INTO cd.facilities
VALUES (9, 'Spa', 20, 30, 100000, 800);

-- Adding a Facility with Auto-Generated ID
INSERT INTO cd.facilities
SELECT (SELECT MAX(facid) FROM cd.facilities) + 1, 'Spa', 20, 30, 100000, 800;

-- Updating Data in a Row
UPDATE cd.facilities
SET initialoutlay = 10000
WHERE facid = 1;

-- Updating Data with Calculations
UPDATE cd.facilities facs
SET
    membercost = facs2.membercost * 1.1,
    guestcost = facs2.guestcost * 1.1
    FROM (SELECT * FROM cd.facilities WHERE facid = 0) facs2
WHERE facs.facid = 1;

-- Deleting All Rows
DELETE FROM cd.bookings;

-- Deleting Rows with a Condition
DELETE FROM cd.members WHERE memid=37;

-- Selecting with a Filter (Fee < 1/50th of Monthly Maintenance)
SELECT facid, name, membercost, monthlymaintenance
FROM cd.facilities
WHERE
    membercost > 0 AND
    (membercost < monthlymaintenance/50.0);

-- Filtering with LIKE Pattern Matching
SELECT * FROM cd.facilities WHERE name LIKE '%Tennis%';

-- Filtering with OR Condition
SELECT *
FROM cd.facilities
WHERE facid = 1 OR facid = 5;

-- Filtering with Date Condition
SELECT memid, surname, firstname, joindate
FROM cd.members
WHERE joindate >= '2012-09-01';

-- Using UNION to Combine Queries
SELECT surname
FROM cd.members
UNION
SELECT name
FROM cd.facilities;

-- Inner Join with Filter
SELECT bks.starttime
FROM cd.bookings bks
         INNER JOIN cd.members mems ON mems.memid = bks.memid
WHERE mems.firstname='David' AND mems.surname='Farrell';

-- Inner Join with Multiple Filters
SELECT bk.starttime AS start, fac.name AS name
FROM cd.facilities fac
         INNER JOIN cd.bookings bk ON fac.facid = bk.facid
WHERE
    fac.name IN ('Tennis Court 2', 'Tennis Court 1') AND
    bk.starttime >= '2012-09-21' AND
    bk.starttime < '2012-09-22'
ORDER BY bk.starttime;

-- Left Outer Join Example
SELECT mem.firstname AS memfname, mem.surname AS memsname, rec.firstname AS recfname, rec.surname AS refsname
FROM cd.members mem
         LEFT OUTER JOIN cd.members rec ON rec.memid = mem.recommendedby
ORDER BY memsname, memfname;

-- Show All Members with Distinct Recommenders
SELECT DISTINCT recs.firstname AS firstname, recs.surname AS surname
FROM
    cd.members mems
        INNER JOIN cd.members recs ON recs.memid = mems.recommendedby
ORDER BY surname, firstname;

-- Show All Members with Their Recommenders
SELECT DISTINCT mems.firstname || ' ' || mems.surname AS member,
                (SELECT recs.firstname || ' ' || recs.surname AS recommender
                 FROM cd.members recs
                 WHERE recs.memid = mems.recommendedby
                )
FROM
    cd.members mems
ORDER BY member;

-- Group By and Order By Example
SELECT recommendedby, COUNT(*)
FROM cd.members
WHERE recommendedby IS NOT NULL
GROUP BY recommendedby
ORDER BY recommendedby;

-- Group By and Order By with Aggregation
SELECT facid, SUM(slots) AS "Total Slots"
FROM cd.bookings
GROUP BY facid
ORDER BY facid;

-- Group By with Condition
SELECT facid, SUM(slots) AS "Total Slots"
FROM cd.bookings
WHERE starttime >= '2012-09-01' AND starttime < '2012-10-01'
GROUP BY facid
ORDER BY SUM(slots);

-- Group By, Order By, and Extract Function
SELECT facid, EXTRACT(month FROM starttime) AS month, SUM(slots) AS "Total Slots"
FROM cd.bookings
WHERE EXTRACT(year FROM starttime) = 2012
GROUP BY facid, month
ORDER BY facid, month;

-- Distinct Count Example
SELECT COUNT(DISTINCT memid) FROM cd.bookings;

-- Inner Join with Group By and Aggregation
SELECT mems.surname, mems.firstname, mems.memid, MIN(bks.starttime) AS starttime
FROM cd.bookings bks
         INNER JOIN cd.members mems ON mems.memid = bks.memid
WHERE starttime >= '2012-09-01'
GROUP BY mems.surname, mems.firstname, mems.memid
ORDER BY mems.memid;

-- Window Function Example
SELECT COUNT(*) OVER(), firstname, surname
FROM cd.members
ORDER BY joindate;

-- Window Function with Row Number
SELECT ROW_NUMBER() OVER(ORDER BY joindate), firstname, surname
FROM cd.members
ORDER BY joindate;

-- Window Function with Subquery and Group By
SELECT facid, total FROM (
                             SELECT facid, SUM(slots) total, RANK() OVER (ORDER BY SUM(slots) DESC) rank
                             FROM cd.bookings
                             GROUP BY facid
                         ) AS ranked
WHERE rank = 1;

-- Formatting Strings
SELECT surname || ', ' || firstname AS name FROM cd.members;

-- Formatting Strings with Filter
SELECT memid, telephone FROM cd.members WHERE telephone ~ '[()]';

-- Window Function with Subquery and Group By
SELECT SUBSTR(mems.surname, 1, 1) AS letter, COUNT(*) AS count
FROM cd.members mems
GROUP BY letter
ORDER BY letter;