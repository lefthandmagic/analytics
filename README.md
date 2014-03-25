Uber Analytics Application
--------------------------

This is a project that tries to provide analytics over uber trips

Build
------
Pre-reqs are java & maven.
The project is mavenized, and uses the normal maven build process.
To start the application run Main.class as a java application

Scripts
--------
Various helper scripts are present under the script directory.
The scripts are in ruby, and you will need to have ruby installed.
1) seed_data.rb -> generates seed data to work with.
2) ingest.rb -> ingest a sample set of seed data in test_data folder
3) query.rb -> runs a test for all types of rest calls exposed through the application (should run ingest.rb with the provided seed data before this)

Implementation
--------------
1) total number of trips -> uses an atomiclong to track total count across multiple threads
2) total number of trips with date range -> uses concurrent hash map to store count of trips for individual day granularity(we trim the time component)
3) total number of trips in last hour -> uses a sorted set to keep the trips in the last hour, on every ingest older entries are removed, retrieval uses a subset from sorted set
4) total number of clients -> we track a unique concurrent hash set of client ids, size of set is number of clients
5) total number of clients with date range -> track unique concurrent hash set by date, compute unique hash set across date range and return size of that set
6) total miles per client -> keeps a running sum of miles per unique client
7) total miles per client by date range -> keep a running sum of miles per unique client per day. Aggregate across date range.
8) Average fare for a specific city -> I've broken down the overall space into 10 * 10 square which provides totally 162 cities, considering the natural order of (-90, +90) latitude   and (-180 + 180) longitude. We compute the city from the latitude longitute and track the average fare for a specific city.
9) Average fare for a specific city by date range -> Same as above, but the average is tracked per city per day and aggregated on request
10) Median Rating for a Driver -> We store the list of ratings using a balanced min and max heap. Access to median is o(1) by just looking at the heads of the heap.


Future Work/Ideas
-----------------
The solution does not currently horizontally scale across machines as it's limited to one service.
This could be bypassed by hashing out the keyspace to multiple machines.
For the sake of simplicity, all the data ingested is stored in memory, assuming infinite memory(which is obviously no production design).
The right thing to do is to store all these hashes in a nosql database in sorted order(Hbase like). This would allow easy date range scans, and the queries can be optimized by distributing key space across a cluster of machines.
