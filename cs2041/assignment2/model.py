#!/web/cs2041/bin/python3.6.3

# written by andrewt@cse.unsw.edu.au October 2017
# as a starting point for COMP[29]041 assignment 2
# https://cgi.cse.unsw.edu.au/~cs2041/assignments/UNSWtalk/

import os, re
from datetime import tzinfo, timedelta, datetime
from flask import Flask, render_template, flash, redirect, url_for, session, request, logging, send_file
from wtforms import Form, StringField, TextAreaField, PasswordField, validators
# from passlib import sha256_crypt
from functools import wraps
students_dir = "dataset-medium"
user = {}
regex = re.compile(r"\b(\w+)\s*:\s*([^:]*)(?=\s+\w+\s*:|$)")
default_img = 'static/images/profile_default.jpg'


# get a student full detail
def fetch_student(id):
    student_details = None
    try:
        details_filename = os.path.join(students_dir, id, "student.txt")

        with open(details_filename) as f:
            details = f.read()
            if details[-1] == '\n':
                details = details[:-1]
            student_details = dict(regex.findall(details))
            user[student_details['zid']] = student_details
            f.close()

        student_details['friends'] = student_details['friends'][1:-1]
        student_details['friends'] = student_details['friends'].replace(
            ', ', ',')
        student_details['friends'] = student_details['friends'].split(',')
        if '' in student_details['friends']:
            student_details['friends'].pop('')
    except Exception as e:
        print(e)
        pass
    return student_details


# get a student public detail
def fetch_student_public_detail(id):
    student_details = fetch_student(id)

    student_details.pop('courses', None)
    student_details.pop('e-mail', None)
    student_details.pop('home_longitude', None)
    student_details.pop('home_latitude', None)
    student_details.pop('password', None)

    return student_details


def get_friends(zid):
    stud = fetch_student_public_detail(zid)
    friends_list = []
    ff_list = []
    for friend in stud['friends']:
        if re.match(r'^z\d{7}', friend):
            to_add = fetch_student_public_detail(friend)
            friends_list.append(to_add)
            #add ff_list
            for ff in to_add['friends']:
                if zid != ff and ff not in stud['friends'] and ff not in ff_list:
                    ff_list.append(ff)
    return (friends_list, ff_list)


# find all students with keyword
def search_student_with_name(keyword):
    search_result_list = []
    match_list = []
    try:
        students = sorted(os.listdir(students_dir))
        for student in students:
            details_filename = os.path.join(students_dir, student,
                                            "student.txt")
            with open(details_filename) as f:
                details = f.read()
                sub = re.findall(r'full_name: .*', details)
                if sub[0].find(keyword) != -1:
                    search_result_list.append(details)
                f.close()

        for details in search_result_list:
            if details[-1] == '\n':
                details = details[:-1]
            student_details = dict(regex.findall(details))

            student_details['friends'] = student_details['friends'][1:-1]
            student_details['friends'] = student_details['friends'].replace(
                ', ', ',')
            student_details['friends'] = student_details['friends'].split(',')
            if '' in student_details['friends']:
                student_details['friends'].pop('')
            match_list.append(student_details)
    except Exception as e:
        print(e)
        pass
    return match_list

# get a post by Id
def fetch_posts_with_keyword(keyword):
    search_result_list = []
    search_result_post = set(search_result_list)
    match_list = []
    try:
        students = sorted(os.listdir(students_dir))
        for student in students:
            # get the post directory
            post_dir = os.path.join(students_dir, student)
            # sort them in order
            posts_path = sorted(os.listdir(post_dir))
            for post in posts_path:
                if re.match(r'[\d-]*.txt', post):
                    details_file = os.path.join(students_dir, student,post)
                    with open(details_file) as f:
                        details = f.read()
                        sub = re.findall(r'message: .*', details)
                        # print(student, post,"message>>>>>>>>>>> ", sub)
                        if len(sub) != 0 and sub[0].find(keyword) != -1:
                            post_id = post[0:-4]
                            if post_id.find('-') != -1:
                               post_id = post_id[0:post_id.find('-')]

                            search_result_list.append((student ,post_id))
                            # print(student, post,(student ,post_id) ,"message>>>>>>>>>>> ", sub)
                        f.close()
        search_result_post = set(search_result_list)
        total =  len(search_result_list)
        print("finally involved files",len(search_result_list),len(search_result_post))
        search_result_list = []
        # optimisation, when the result is more than 15, paginations
        if len(search_result_post) >= 20:
            return ['too many', total]
        for zid, pid in search_result_post:
            search_result_list.append(fetch_post(zid, pid))
    except Exception as e:
        print(e)
        pass
    return search_result_list



def get_friends_friends(zid):
    friends_list = []
    # get the friends first
    friends, ffriends = get_friends(zid)
    for ff in ffriends:
        to_add = fetch_student_public_detail(ff)
        friends_list.append(to_add)
    return friends_list


def signup(name, email, zid, password, DOB):
    stud = fetch_student(zid)
    if stud == None:
        new_dir = os.path.join(students_dir, zid)
        os.makedirs(new_dir)
        new_path = os.path.join(new_dir, 'student.txt')
        f = open(new_path, 'w')
        f.write("full_name: " + name)
        f.write("\nbirthday: " + DOB)
        f.write("\npassword: " + password)
        f.write("\nzid: " + zid)
        f.write("\nemail: " + email)
        f.write(
            """\nprogram:\nfriends:()\nhome_suburb:\nhome_longtitude:\nhome_latitude: \ncourse:()\n"""
        )
        f.close()
    else:
        return False

    return True


def get_now():
    return datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%f")


def savePost(message, zid, lati, long):
    try:
        id = get_new_post_ID(zid)
        messages = message.split('\r\n')
        message = '\\n'.join(messages)
        # get the post directory
        post_dir = os.path.join(students_dir, zid)
        post_path = os.path.join(post_dir, str(id) + '.txt')
        f = open(post_path, 'w')
        f.write("message: " + message)
        f.write("\nfrom: " + zid)
        f.write("\ntime: " + get_now())
        f.write("\nlongitude: " + long)
        f.write("\nlatitude: " + lati)
        f.close()
    except Exception as e:
        print(e)
        pass
    return id


def get_new_post_ID(zid):
    posts_id = []
    # get the post directory
    post_dir = os.path.join(students_dir, zid)
    # sort them in order
    posts_path = sorted(os.listdir(post_dir))
    # get all files related to this zid user(files has no dash)
    for post in posts_path:
        if re.match(r'\d*.txt', post):
            posts_id.append(post)
    return len(posts_id)


def saveComment(pid, message, zid):
    id = '-1'
    try:
        id = get_new_comment_ID(zid, pid)
        messages = message.split('\r\n')
        message = '\\n'.join(messages)
        # get the post directory
        post_dir = os.path.join(students_dir, zid, pid)
        comment_path = post_dir + '-%s.txt' % str(id)
        f = open(comment_path, 'w')
        f.write("message: " + message)
        f.write("\nfrom: " + zid)
        f.write("\ntime: " + get_now())
        f.close()
    except Exception as e:
        print(e)
        pass
    return id


def get_new_comment_ID(zid, pid):
    comments_id = []
    # get the post directory
    post_dir = os.path.join(students_dir, zid)
    # sort them in order
    posts_path = sorted(os.listdir(post_dir))
    # get all files related to this zid user(files has no dash)
    for post in posts_path:
        if re.match(r'\d*-\d*.txt', post) and str.startswith(
                post, str(pid) + '-'):
            comments_id.append(post)
    return len(comments_id)


def saveReply(pid, cid, message, zid):
    id = '-1'
    try:
        id = get_new_reply_ID(zid, pid, cid)
        messages = message.split('\r\n')
        message = '\\n'.join(messages)
        # get the post directory
        post_dir = os.path.join(students_dir, zid, pid)
        comment_path = post_dir + '-%s-%s.txt' % (str(cid), str(id))
        f = open(comment_path, 'w')
        f.write("message: " + message)
        f.write("\nfrom: " + zid)
        f.write("\ntime: " + get_now())
        f.close()
    except Exception as e:
        print(e)
        pass
    return id


def get_new_reply_ID(zid, pid, cid):
    replys_id = []
    # get the post directory
    post_dir = os.path.join(students_dir, zid)
    # sort them in order
    posts_path = sorted(os.listdir(post_dir))
    # get all files related to this zid user(files has no dash)
    for post in posts_path:
        if re.match(r'\d*-\d*-\d*.txt', post) and str.startswith(
                post, '%s-%s-' % (str(pid), str(cid))):
            replys_id.append(post)
    return len(replys_id)










# get all posts for zid
def fetch_posts_list(zid):
    # the goal data to return
    posts = []
    try:
        posts_id = []
        # get the post directory
        post_dir = os.path.join(students_dir, zid)
        # sort them in order
        posts_path = sorted(os.listdir(post_dir))
        # get all files related to this zid user(files has no dash)
        for post in posts_path:
            if re.match(r'^\d*.txt', post):
                posts_id.append(post)
        posts_id.reverse()
        for post in posts_id:
            posts.append(fetch_post(zid, post[0:-4]))
    except Exception as e:
        print(e)
        pass
    return posts


# # get a post by Id
# def fetch_post_list(zid, id):
#     # the goal data to return
#     post_details = {}
#     try:
#         # get the post directory
#         post_dir = os.path.join(students_dir, zid)
#         # sort them in order
#         posts = sorted(os.listdir(post_dir))
#         # get all files related to this post id
#         post = []
#         for p in posts:
#             if str.startswith(p, id):
#                 post.append(p)
#         # sort the files in order
#         post = sorted(post, key=len)
#         # get the post content
#         root = post.pop(0)
#         root_post_path = os.path.join(post_dir, root)
#         with open(root_post_path, 'r') as f:
#             details = f.read()
#             form_msg_detail(post_details, details)
#         f.close()
#     except Exception as e:
#         print(e)
#         pass
#     post_details['id'] = id
#     return post_details


# get all posts for zid
def fetch_all_post(zid):
    # the goal data to return
    posts = []
    try:
        posts_id = []
        # get the post directory
        post_dir = os.path.join(students_dir, zid)
        # sort them in order
        posts_path = sorted(os.listdir(post_dir))
        # get all files related to this zid user(files has no dash)
        for post in posts_path:
            if re.match(r'^\d*.txt', post):
                posts_id.append(post)
        for post in posts_id:
            posts.append(fetch_post(zid, post[0:-4]))
    except Exception as e:
        print(e)
        pass
    return posts


# get a post by Id
def fetch_post(zid, id):
    # the goal data to return
    post_details = {'comments': []}
    try:
        # get the post directory
        post_dir = os.path.join(students_dir, zid)
        # sort them in order
        posts = sorted(os.listdir(post_dir))
        # get all files related to this post id
        post = []
        for p in posts:
            if str.startswith(p, id):
                post.append(p)
        # sort the files in order
        post = sorted(post, key=len)
        # get the post content
        root = post.pop(0)
        root_post_path = os.path.join(post_dir, root)
        with open(root_post_path, 'r') as f:
            details = f.read()
            form_msg_detail(post_details, details)
            f.close()

        while len(post) > 0:
            comment, replies = rec_read_res(post)
           
            comment_path = os.path.join(post_dir, comment)
            temp_details = {
                'id': comment[comment.find('-')+1:-4],
                'replies': []
            }
            with open(comment_path, 'r') as f:
                details = f.read()
                form_msg_detail(temp_details, details)
                post_details['comments'].append(temp_details)
                f.close()
            for reply in replies:
                reply_path = os.path.join(post_dir, reply)
                tem_dtl = {'id': reply[reply.find('-')+1:-4]}
                with open(reply_path) as f:
                    details = f.read()
                    form_msg_detail(tem_dtl, details)
                    temp_details['replies'].append(tem_dtl)
                    f.close()
            temp_details['replies'].reverse()
            post_details['comments'].reverse()
    except Exception as e:
        print(e)
        pass
    post_details['id'] = root_post_path.replace(post_dir+'/','')[:-4]
    return post_details


def form_msg_detail(post_details, details):
    lines = sorted(details.split('\n'))
    lines.reverse()
    post_details['user'] = {}
    for i in lines:
        if len(i) != 0 and not str.startswith(i, 'time'):
            key, sep, val = i.partition(':')

            post_details[key] = val
            if key == 'from':
                val = val[1:]
                fetch_post_users(val, post_details)
            if key == 'message':
                val = val.replace('\\n', '  <br>  ')
                res = re.findall(r'z\d{6,}', val)
                for i in res:
                    sub = i + ' '
                    val = val.replace(i, sub)
                vals = val.split()
                for e in res:
                    fetch_post_users(e, post_details)
                post_details[key] = vals
        elif str.startswith(i, 'time'):
            post_details['time'] = i[6:]
    return post_details


def fetch_post_users(val, post_details):
    if val in user:
        u = user[val]['full_name']
        post_details['user'][val] = u
    else:
        u = fetch_student(val)['full_name']
        post_details['user'][val] = u


def rec_read_res(posts_path):
    child = []
    # get the comment
    path = posts_path.pop(0)
    for item in posts_path:
        # find the replies
        if str.startswith(item, path.replace('.txt','-')):
            child.append(item)
    # remove replies and comment in the posts_path
    for item in child:
        posts_path.pop(posts_path.index(item))
    return (path, child)


def test():
    fetch_posts_with_keyword("z5191238")
    pass


# get a post by Id
def delete_post(zid, id):
    try:
        # get the post directory
        post_dir = os.path.join(students_dir, zid)
        # sort them in order
        posts = sorted(os.listdir(post_dir))
        # get all files related to this post id
        post = []
        i = 0
        for p in posts:
            if str.startswith(p, id):
                i += 1
                os.remove(os.path.join(post_dir, p))
        posts = sorted(os.listdir(post_dir))
    except Exception as e:
        print(e)
        pass
    return


if __name__ == "__main__":
    test()
    # assert post['time'] == '2016-05-07T16:19:43+0000'
