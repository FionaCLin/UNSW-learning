#!/web/cs2041/bin/python3.6.3

# written by andrewt@cse.unsw.edu.au October 2017
# as a starting point for COMP[29]041 assignment 2
# https://cgi.cse.unsw.edu.au/~cs2041/assignments/UNSWtalk/

import os, re, shutil
from flask import Flask, render_template, flash, redirect, url_for, session, request, logging, send_file
from wtforms import Form, StringField, TextAreaField, PasswordField, validators, DecimalField
from wtforms.fields.html5 import DateField
# from passlib import sha256_crypt
from functools import wraps
import model

students_dir = "dataset-medium"
user = {}
regex = re.compile(r"\b(\w+)\s*:\s*([^:]*)(?=\s+\w+\s*:|$)")
default_img = 'static/images/profile_default.jpg'
app = Flask(__name__)


# Show unformatted details for student "n"
# Increment n and store it in the session cookie
@app.route('/', methods=['GET', 'POST'])
def index():
    # dir = url_for('index')
    return render_template('home.html')


# User Register
@app.route('/register', methods=['GET', 'POST'])
def register():
    form = RegisterForm(request.form)
    if request.method == 'POST' and form.validate():

        name = form.name.data
        email = form.email.data
        zid = form.zid.data
        password = str(form.password.data)
        DOB = form.DOB.data.strftime('%Y-%m-%d')
        if model.signup(name, email, zid, password, DOB):
            flash('You are now registered and can log in', 'success')
            return redirect(url_for('login'))
        else:
            error = 'ZID already exist'
            return render_template('register.html', error=error, form=form)
    return render_template('register.html', form=form)


# Register Form Class
class RegisterForm(Form):
    name = StringField('Full Name', [
        validators.Length(min=8, max=50),
    ])
    zid = StringField('zid', [validators.Length(min=8)])
    email = StringField('Email', [validators.Length(min=6, max=35)])
    DOB = DateField('DatePicker', format='%Y-%m-%d')
    password = PasswordField('Password', [
        validators.Required(),
        validators.Length(min=6, max=50),
        validators.EqualTo('confirm', message='Passwords do not match')
    ])
    confirm = PasswordField('Confirm Password')


# User login
@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        # Get Form Fields
        zid = request.form['zid']
        password_candidate = request.form['password']

        stud = model.fetch_student(zid)
        if stud != None:

            if stud['password'] == password_candidate:  # change to condition check
                # Passed
                session['logged_in'] = True
                session['user'] = stud

                flash('You are now logged in', 'success')
                return redirect(url_for('index'))
            else:
                error = 'Invalid login'
                return render_template('login.html', error=error)
        else:
            error = 'zid not found'
            return render_template('login.html', error=error)

    return render_template('login.html')


# Check if user logged in
def is_logged_in(f):
    @wraps(f)
    def wrap(*args, **kwargs):
        if 'logged_in' in session:
            return f(*args, **kwargs)
        else:
            flash('Unauthorized User, Please login', 'danger')
            return redirect(url_for('login'))

    return wrap


# check user is logged in otherwise redirect user to login
@app.route('/logout', methods=['GET', 'POST'])
@is_logged_in
def logout():
    session.clear()
    flash('You are now logged out', 'success')
    return redirect(url_for('login'))


# an route to fetch a picture of zid user
@app.route('/img/<string:id>', methods=['GET', 'POST'])
@is_logged_in
def img(id):
    img_filename = os.path.join(students_dir, id, "img.jpg")
    
    print('$#@!$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$!@#$',img_filename)
    if not os.path.exists(img_filename):
        img_filename = default_img
    return send_file(img_filename, mimetype='image/jpg')


"""
Friends section
"""


#friends profile page
@app.route('/profile/<string:zid>', methods=['GET', 'POST'])
@is_logged_in
def profile(zid):
    details_filename = os.path.join(students_dir, zid, "student.txt")
    student_details = model.fetch_student_public_detail(zid)
    if student_details['zid'] == session['user']['zid']:
        return redirect(url_for('account'))
    return render_template('profile.html', student_details=student_details)


# get the list of friends of user
@app.route('/friends', methods=['GET', 'POST'])
@is_logged_in
def friends():
    zid = session['user']['zid']
    print('zid', zid)
    friends_list, ffs = model.get_friends(zid)
    ffs = model.get_friends_friends(zid)
    return render_template('friends.html', friends=friends_list, ff_list=ffs)


#need to change the route
@app.route('/account', methods=['GET', 'POST'])
@is_logged_in
def account():
    students = sorted(os.listdir(students_dir))
    student_to_show = session['user']['zid']
    student_details = model.fetch_student_public_detail(student_to_show)
    return render_template('account.html', student_details=student_details)


class PostCreateForm(Form):
    message = TextAreaField('Message', [validators.Length(min=1, max=500)])
    lati = DecimalField('Latitude', places=2, rounding=None)
    long = DecimalField('Longtitude', places=2, rounding=None)


"""
Posts section
"""


# create post
@app.route('/post', methods=['GET', 'POST'])
@is_logged_in
def add_post():
    form = PostCreateForm(request.form)
    if request.method == 'POST' and form.validate():
        zid = session['user']['zid']
        message = form.message.data
        lati = '%.4f' % form.lati.data
        long = '%.4f' % form.long.data
        id = model.savePost(message, zid, lati, long)
        flash('Post Created', 'success')
        return redirect(url_for('post', id=id))
    return render_template('add_post.html', form=form)


# get all post as list for this sesion user
@app.route('/posts', methods=['GET', 'POST'])
@is_logged_in
def posts():
    zid = session['user']['zid']
    posts_list = model.fetch_posts_list(zid)
    return render_template('posts_list.html', posts=posts_list)


# fetch a post and its comment and reply by the id
@app.route('/post/<string:id>', methods=['GET', 'POST'])
@is_logged_in
def post(id):
    zid = session['user']['zid']
    post_detail = model.fetch_post(zid, id)
    print(post_detail)
    return render_template(
        'post_detail.html', post_detail=post_detail, form=None)


# fetch a post and its comment and reply by the id and deletion post
@app.route('/deletpost/<string:id>')
@is_logged_in
def dlt_post(id):
    zid = session['user']['zid']
    res = model.delete_post(zid, id)
    posts_list = model.fetch_posts_list(zid)
    flash('Post %s delete' % id, 'success')
    return render_template('posts_list.html', posts=posts_list)


"""
Comment and reply create section
"""


class CommentReplyCreateForm(Form):
    message = TextAreaField('Message', [validators.Length(min=1, max=500)])


# create comment
@app.route('/post/<string:pid>/comment', methods=['GET', 'POST'])
@is_logged_in
def add_comment(pid):
    print(pid)
    form = CommentReplyCreateForm(request.form)
    if request.method == 'POST' and form.validate():
        zid = session['user']['zid']
        message = form.message.data
        cid = model.saveComment(pid, message, zid)
        flash('Comment Created', 'success')
        return redirect(url_for('post', id=pid))
    zid = session['user']['zid']
    post_detail = model.fetch_post(zid, pid)
    return render_template(
        'post_detail.html',
        post_detail=post_detail,
        form=form,
        title="Comment")


# create reply
@app.route('/post/<string:pid>/comment/<string:cid>/reply', methods=['GET', 'POST'])
@is_logged_in
def add_reply(pid, cid):
    form = CommentReplyCreateForm(request.form)
    if request.method == 'POST' and form.validate():
        zid = session['user']['zid']
        message = form.message.data
        cid = model.saveReply(pid, cid, message, zid)
        flash('Reply Created', 'success')
        return redirect(url_for('post', id=pid))
    zid = session['user']['zid']
    post_detail = model.fetch_post(zid, pid)
    return render_template(
        'post_detail.html', post_detail=post_detail, form=form, title="Reply")


#need to change the route
@app.route('/searchfriends', methods=['POST','GET'])
@is_logged_in
def findfriends():
    full_name = request.form['full_name']
    people = model.search_student_with_name(full_name)
    return render_template('search_name.html', friends=people, keyword=full_name)


#need to change the route
@app.route('/searchposts', methods=['POST','GET'])
@is_logged_in
def findposts():
    keyword = request.form['keyword']
    print(keyword)
    posts_list = model.fetch_posts_with_keyword(keyword)
    if len(posts_list) == 2 and posts_list[0] == 'too many':
        error = 'Too many found entries, please refine the keywords'
        print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",posts_list[1])
        return render_template('search_posts.html',posts=[], keyword = keyword, total=posts_list[1],error=error)    
    return render_template('search_posts.html',posts=posts_list, keyword = keyword, total=len(posts_list))



if __name__ == '__main__':
    app.secret_key = os.urandom(12)
    app.run(debug=True)
"""
birthday: 1999-05-23
friends: (z5195935, z5191824, z5197433, z5196487, z5195928)
home_latitude: -33.7515
home_longitude: 151.0559
program: Economics/Advanced Maths(Hons)
full_name: Amelia Vega
zid: z5193755
password: racing
email: z5193755@unsw.edu.au
home_suburb: Beecroft
courses: (2016 S1 ACCT1501, 2016 S1 ECON1101, 2016 S1 MATH1141, 2016 S1 SCIF1121, 2016 S2 ECON1102, 2016 S2 ECON1401, 2016 S2 ECON2101, 2016 S2 MATH1241, 2017 S1 COMP1911, 2017 S1 ECON2102, 2017 S1 ECON2127, 2017 S1 ECON2206, 2017 S2 ECON3121, 2017 S2 MATH1081, 2017 S2 MATH2221)
"""
