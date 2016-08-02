Few words of introduction
===================

Task has been performed applying lean software development principles, especially eliminate waste and **KISS** (keep it short and simple).

In seems that requirements are clear, but there is a need to make research and/or make some assumptions.

----------


Design / Assumptions / Decisions
-------------

 1. Logic is centered around domain. 

	Domain objects should know how to behave depending on domain context, separation of domain logic from domain objects is unnatural and is making objects to be just a repository of data, anemic value objects without behaviour. Capturing domain logic in so called service objects that operate on a domain model treating it just like a set of data is anti object oriented and and puts responsibilities in the place they do not belong to.

	Of course, there is a place for service objects, but they have different responsibilities, for example communication with external resources, exposing stock index in a form of REST API. Services should be an interface used by other components that cannot interact/communicate with/via domain objects, also if there is such need service objects could perform logic not strictly and tightly related to business domain itself.
	
 2. Public contracts

	I was considering creating interfaces for classes to explicitly expose a public contract, but they can be easily introduced if there is such need - let's keep it short and simple.
	
 3. Favor composition over inheritance

	There was a temptation to favor composition over inheritance and use strategy pattern for formulas, but probably formulas will not change or at least will not change often and dynamic behavior change for domain objects is not necessary - let's keep it short and simple.

 4. Performance

	Probably index computation etc. could be done in parallel to make it faster, but we do not know if performance would be an issue at all in this system - let's keep it short and simple.

 5. Trading

	*"For a given stock record a trade, with timestamp, quantity, buy or sell indicator and price"*.

	I intepreted this requirement that not only there is a need to simply record provided trade data (without taking into account that some trades with earlier timestamps could be provided at a later time after trades with later timestamps has already been provided), but trades can be done live and it is mandatory to make sure that data is not corrupted.

	I used read-write lock instead of mutual exclusion synchronization, performance depends on frequency that the data is read compared to being written - there is not enough information on how often data could be read, it is just my assumption it could perform better.
	
	Trade has been made immutable, it seems that there is no need to make any changes once the object is created. The only issue is that total of the trade is not lazily initialized then.
