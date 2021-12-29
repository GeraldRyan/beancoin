1. See if you can extract all session attributes into own class, or put them into @service class. Should not have to 
   keep repeating myself in terms of getAttribute and if null, then get from database (and for performance reasons 
   should not go straight to database)
If you can put session attributes into @Service class, that would be better handled there. Keep the @Controller simpler

Moreover, make a single function that takes a name argument and does a null check to see if that returns null. If 
not, then get the item from the database.

2. Differentiate what goes in session attributes (user specific things) and what is not a session attribute but more 
   properly a cached attribute for ALL users - e.g. blockchain itself, which is a shared object, of course. There's 
   no blockchain for me and another blockchain for thee. But it is a highly cacheable entity. Before implementing 
   cache on blockchain, better to just remove it from session variable. 


Tempted to delete all session attributes and put them back only as needed. Sacrifice performance initially for 
clarity and put back as needed, to add back the performance.

Simply just always pull from database. I think blockchain as session attribute when only one user (me) functioned as 
a de facto cache. 

Session is a cache- for a specific user. It is a specific user cache. Besides performance, it also means one doesn't 
need to make a call to the database if you know it exists in memory. 

The problem is I learned that too quickly to understand it. 

THe model has a lifecycle of a single request. The session is a cache and who knows the lifecycle or length of how 
it lives before it's purged? That would be good to learn how that works. Look at docs. 

The model is the gateway to the cache. When something is added to the model, the model looks and sees if it's in the 
list of session attributes. If it is, it puts it there and it puts it there whenever it changes. And the model is 
simply a map so the session is likewise a map, with a subset of the keys (or that's all it need be, at any rate)