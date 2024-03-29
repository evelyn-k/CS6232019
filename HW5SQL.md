1.	What are the #prods whose name begins with a ’p’ and are less than
$300.00?

```
SELECT prod_id
FROM product 
WHERE pname LIKE 'p%' AND price < 300;
```
2.	Names of the products stocked in ”d2”.

(a)	without in/not in
```
SELECT p.pname 
FROM product p 
WHERE p.prod_id IN (SELECT prod_id FROM stock WHERE dep_id = 'd2');	
```
(b)	with in/not in
```
SELECT p.pname 
FROM product p 
WHERE p.prod_id IN (SELECT prod_id FROM stock WHERE dep_id = 'd2');	
```
3.	#prod and names of the products that are out of stock.

(a)	without in/not in
```
SELECT p.prod_id, p.pname
FROM product p, stock s
WHERE p.prod_id = s.prod_id AND s.quantity < 1;
```
(b)	with in/not in
```
SELECT prod_id, pname
FROM product 
WHERE prod_id IN (SELECT prod_id FROM stock WHERE quantity < 1);
```
4.	Addresses of the depots where the product ”p1” is stocked.

(a)	without exists/not exists and without in/not in
```
SELECT d.addr
FROM depot d
WHERE d.dep_id=s.dep_id AND s.prod_id = ‘p1’ AND s.quantity > 0;
```
(b)	with in/not in 
```
SELECT addr 
FROM depot 
WHERE dep_id IN (SELECT dep_id FROM stock WHERE prod_id = 'p1' AND quantity > 0);
```
(c)	with exists/not exists
```
SELECT addr
FROM depot
WHERE EXISTS (SELECT dep_id FROM stock WHERE prod_id = ‘p1’ AND quantity > 0);
```
5.	#prods whose price is between $250.00 and $400.00.

(a)	using intersect.
```
SELECT prod_id
FROM product 
WHERE price >= 250.0
INTERSECT 
SELECT prod_id 
FROM product 
WHERE price < 400.0;
Note: there is a syntax error here, unfortunately couldn’t figure it out. Will work on it after submission. 
```
(b)	without intersect.
```
SELECT prod_id 
FROM product 
WHERE price BETWEEN 250.0 AND 400.0;
```
6.	How many products are out of stock?
```
SELECT count(*) 
FROM stock 
GROUP BY prod_id, dep_id
HAVING quantity < 1;
```
7.	Average of the prices of the products stocked in the ”d2” depot.
```
SELECT p.prod_id, AVG(p.price) 
FROM product p JOIN stock s ON p.prod_id = s.prod_id AND s.dep_id = 'd2' AND s.quantity > 0;
```
8.	#deps of the depot(s) with the largest capacity (volume).
```
SELECT dep_id 
FROM depot 
WHERE volume = (SELECT max(volume) FROM depot);
```
9.	Sum of the stocked quantity of each product.
```
SELECT prod_id, SUM(quantity) 
FROM stock 
GROUP BY prod_id;
```
10.	Products names stocked in at least 3 depots.

(a)	using count
```
SELECT pname 
FROM product 
WHERE prod_id IN (SELECT prod_id FROM stock GROUP BY prod_id HAVING count(prod_id) >= 3);
```
(b)	without using count

11.	#prod stocked in all depots.

(a)	using count
```
SELECT COUNT(dep_id), prod_id
FROM stock
GROUP BY prod_id HAVING COUNT (dep_id) = 3;
```
(b)	using exists/not exists
```
SELECT EXISTS ( 
SELECT dep_id
FROM stock
WHERE dep_id = “d1’, ‘d2’, ‘d3”);
```
