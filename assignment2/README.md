COMP[29]041 assignment 2
http://www.cse.unsw.edu.au/~cs2041/assignments/UNSWtalk

<h2 id="milestones">Milestones<a aria-label="Anchor" class="anchorjs-link " data-anchorjs-icon="" href="https://cgi.cse.unsw.edu.au/~cs2041/18s2/assignments/ass2/index.html#milestones" style="font: 1em/1 anchorjs-icons; padding-left: 0.375em;"></a></h2>
<p>Level 0 focuses on the basic user interface and interaction building of the site.
There is no need to implement any integration with the backend for this level.</p>
<h2 id="level-0">Level 0<a aria-label="Anchor" class="anchorjs-link " data-anchorjs-icon="" href="https://cgi.cse.unsw.edu.au/~cs2041/18s2/assignments/ass2/index.html#level-0" style="font: 1em/1 anchorjs-icons; padding-left: 0.375em;"></a></h2>
<p><strong>Login</strong>
The site presents a login form and a user can log in with pre-defined hard coded credentials.
You can use the provided users.json so you can create a internal non persistent list of users that you check against.</p>
<p>Once logged in, the user is presented with the home page which for now can be a blank page with a simple “Not Yet implemented” message.</p>
<p><strong>Registration</strong>
An option to register for “Instacram” is presented on the login page allowing the user to sign up to the service.
This for now updates the internal state object described above.</p>
<p><strong>Feed Interface</strong></p>
<p>The application should present a “feed” of user content on the home page derived from the sample feed.json provided.
The posts should be displayed in reverse chronological order (most recent posts first). You can hardcode how this works for
this milestone.</p>
<p>Although this is not a graphic design exercise you should produce pages with a common and somewhat distinctive look-and-feel. You may find CSS useful for this.</p>
<p>Each post must include:</p>
<ol>
<li>who the post was made by</li>
<li>when it was posted</li>
<li>The image itself</li>
<li>How many likes it has (or none)</li>
<li>The post description text</li>
<li>How many comments the post has</li>
</ol>
<h2 id="level-1">Level 1<a aria-label="Anchor" class="anchorjs-link " data-anchorjs-icon="" href="https://cgi.cse.unsw.edu.au/~cs2041/18s2/assignments/ass2/index.html#level-1" style="font: 1em/1 anchorjs-icons; padding-left: 0.375em;"></a></h2>
<p>Level 1 focuses on fetching data from the API.</p>
<p><strong>Login</strong>
The site presents a login form and verifies the provided credentials with the backend (<code class="highlighter-rouge">POST /login</code>). Once logged in, the user can see the home page.</p>
<p><strong>Registration</strong>
An option to register for “Instacram” is presented allowing the user to sign up to the service. The user information is POSTed to the backend to create the user in the database. (<code class="highlighter-rouge">POST /signup</code>)</p>
<p><strong>Feed Interface</strong>
The content shown in the user’s feed is sourced from the backend. (<code class="highlighter-rouge">GET /user/feed</code>)</p>
<h2 id="level-2">Level 2<a aria-label="Anchor" class="anchorjs-link " data-anchorjs-icon="" href="https://cgi.cse.unsw.edu.au/~cs2041/18s2/assignments/ass2/index.html#level-2" style="font: 1em/1 anchorjs-icons; padding-left: 0.375em;"></a></h2>
<p>Level 2 focuses on a richer UX and will require some backend interaction.</p>
<p><strong>Show Likes</strong>
Allow an option for a user to see a list of all users who have liked a post.
Possibly a modal but the design is up to you.</p>
<p><strong>Show Comments</strong>
Allow an option for a user to see all the comments on a post.
Same as above.</p>
<p><strong>Like user generated content</strong>
A logged in user can like a post on their feed and trigger an API request (<code class="highlighter-rouge">PUT /post/like</code>)
For now it’s ok if the like doesn’t show up until the page is refreshed.</p>
<p><strong>Post new content</strong>
Users can upload and post new content from a modal or seperate page via (<code class="highlighter-rouge">POST /post</code>)</p>
<p><strong>Pagination</strong>
Users can page between sets of results in the feed using the position token with (<code class="highlighter-rouge">GET user/feed</code>).
Note users can ignore this if they properly implement Level 3’s Infinite Scroll.</p>
<p><strong>Profile</strong>
Users can see their own profile information such as username, number of posts, sum of likes they received on all their posts, etc.
You may choose to utilise the information from the api in more creative ways such as displaying their most liked post etc.
Get this information from (<code class="highlighter-rouge">GET /user</code>)</p>
<h2 id="level-3">Level 3<a aria-label="Anchor" class="anchorjs-link " data-anchorjs-icon="" href="https://cgi.cse.unsw.edu.au/~cs2041/18s2/assignments/ass2/index.html#level-3" style="font: 1em/1 anchorjs-icons; padding-left: 0.375em;"></a></h2>
<p>Level 3 focuses on more advanced features that will take time to implement and will
involve a more rigorously designed app to execute.</p>
<p><strong>Infinite Scroll</strong>
Instead of pagination, users can infinitely scroll through results. For infinite scroll to be
properly implemented you need to progressively load posts as you scroll.</p>
<p><strong>Comments</strong>
Users can write comments on “posts” via (<code class="highlighter-rouge">POST post/comment</code>)</p>
<p><strong>Live Update</strong>
If a user likes a post or comments on a post, the posts likes and comments should
update without requiring a page reload/refresh.</p>
<p><strong>Update Profile</strong>
Users can update their personal profile via (<code class="highlighter-rouge">PUT /user</code>) E.g:</p>
<ul>
<li>Update email address</li>
<li>Update password</li>
<li>Update name</li>
</ul>
<p><strong>User Pages</strong>
Let a user click on a user’s name/picture from a post and see a page with the users name, and other info.
The user should also see on this page all posts made by that person.
The user should be able to see their own page as well.</p>
<p>This can be done as a modal or as a seperate page (URL fragmentation can be implemented if desired.)</p>
<p><strong>Follow</strong>
Let a user follow/unfollow another user to add/remove their posts to their feed via (<code class="highlighter-rouge">PUT user/follow</code>)
Add a list of everyone a user follows in their profile page.
Add just the count of followers / follows to everyones public user page
</p>
<p>
  To allow users to find other users implement some functionality to check if a username exists and allow the user to follow them.
  This may be as simple as a text box in the navbar which signals when a inputted username is wrong or if it's right gives you a option
  to follow the user.
  You do not need to implement anything more advanced such as searching etc.
</p>
<p><strong>Delete/Update Post</strong>
Let a user update a post they made or delete it via (<code class="highlighter-rouge">DELETE /post</code>) or (<code class="highlighter-rouge">PUT /post</code>)</p>
<h2 id="level-4">Level 4<a aria-label="Anchor" class="anchorjs-link " data-anchorjs-icon="" href="https://cgi.cse.unsw.edu.au/~cs2041/18s2/assignments/ass2/index.html#level-4" style="font: 1em/1 anchorjs-icons; padding-left: 0.375em;"></a></h2>
<p><strong>Slick UI</strong>
The user interface looks good, is performant, makes logical sense, and is usable.</p>
<p><strong>Push Notifications</strong>
Users can receive push notifications when a user they follow posts an image.</p>
<p><strong>Offline Access</strong>
Users can access the “Instacram” at all times by using Web Workers to cache the page (and previous content) locally.</p>
<p><strong>Fragment based URL routing</strong>
Users can access different pages using URL fragments:</p>
<div class="highlighter-rouge">
<div class="highlight">
<pre class="highlight"><code>/#profile=me
/#feed
/#profile=janecitizen</code></pre>
</div>
</div>
