import socket
import select
import sys
import tkinter as tk
import tkinter.messagebox


# a class for client
class Client:
    # initialize the client
    def __init__(self):
        # get server's host and port and client's host and port
        server_host = sys.argv[1]
        server_port = int(sys.argv[2])
        host = socket.gethostname()
        port = int(sys.argv[3])
        # set up the client socket
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind((host, port))
        self.socket.connect((server_host, server_port))
        # initialize variables
        self.current_window = None  # the current gui window
        self.after_id = None  # the after_id for timing operation
        self.username = None  # the username for the client

    # close connection and gui window
    def close(self):
        # if has username (means login success), logout
        if self.username:
            msg = "logout " + self.username
        else:
            msg = "close"
        self.socket.sendall(msg.encode())
        self.current_window.destroy()
        self.socket.close()

    # set a button
    def set_button(self, name):
        btn = tk.Button(self.current_window, text=name, command=lambda: self.choose_action(name))
        btn.pack()

    # the layout for menu window, there are 2 or 3 command buttons. This layout is used for following windows:
    #   the menu window before login (has register and login buttons)
    #   the menu window after login (has list, message, logout buttons)
    def menu_layout(self, btn1_name, btn2_name, btn3_name=None):
        # close previous window if exist
        if self.current_window:
            self.current_window.after_cancel(self.after_id)
            self.current_window.destroy()
        # create new window
        self.current_window = tk.Tk()
        title = "client"
        if self.username:
            title = title + "-" + self.username
        self.current_window.title(title)
        self.current_window.geometry('500x200')
        # set buttons
        self.set_button(btn1_name)
        self.set_button(btn2_name)
        if btn3_name:
            self.set_button(btn3_name)
        # receiving data every 100ms
        self.after_id = self.current_window.after(100, self.receive)
        # set close window event
        self.current_window.protocol('WM_DELETE_WINDOW', self.close)
        # set mainloop to interactive with user
        self.current_window.mainloop()

    # the layout for input window, there are two input boxes. This layout is used for following windows:
    #   the input window for register
    #   the input window for login
    #   the input window for message
    def input_layout(self, flag, lbl1_name, lbl2_name):
        # close previous window
        self.current_window.after_cancel(self.after_id)
        self.current_window.destroy()
        # create new window
        self.current_window = tk.Tk()
        title = "client"
        if self.username:
            title = title + "-" + self.username
        self.current_window.title(title)
        self.current_window.geometry('500x200')
        # set input boxes
        lbl1 = tk.Label(self.current_window, text=lbl1_name)
        lbl1.pack()
        etr1 = tk.Entry(self.current_window, show=None)
        etr1.pack()
        lbl2 = tk.Label(self.current_window, text=lbl2_name)
        lbl2.pack()
        if flag == "Message":
            etr2 = tk.Entry(self.current_window, show=None)
        else:
            etr2 = tk.Entry(self.current_window, show='*')
        etr2.pack()
        # set submit and return buttons
        submit_btn = tk.Button(self.current_window, text="Submit",
                               command=lambda: self.handle_submit(flag, etr1.get(), etr2.get()))
        submit_btn.pack()
        return_btn = tk.Button(self.current_window, text="Return", command=lambda: self.choose_action(flag + "_return"))
        return_btn.pack()
        # receiving data every 100ms
        self.after_id = self.current_window.after(100, self.receive)
        # set close window event
        self.current_window.protocol('WM_DELETE_WINDOW', self.close)
        # set mainloop to interactive with user
        self.current_window.mainloop()

    # the window for list
    def list_window(self, lists):
        # close previous window
        self.current_window.after_cancel(self.after_id)
        self.current_window.destroy()
        # create new window
        self.current_window = tk.Tk()
        self.current_window.title('client-' + self.username)
        self.current_window.geometry('500x200')
        # show the list message
        lbl = tk.Label(self.current_window, text=lists)
        lbl.pack()
        # set return button
        return_btn = tk.Button(self.current_window, text="Return", command=lambda: self.choose_action("List_return"))
        return_btn.pack()
        # receiving data every 100ms
        self.after_id = self.current_window.after(100, self.receive)
        # set close window event
        self.current_window.protocol('WM_DELETE_WINDOW', self.close)
        # set mainloop to interactive with user
        self.current_window.mainloop()

    # handle register command
    def register(self, name, password):
        msg = "register " + name + " " + password
        self.socket.sendall(msg.encode())

    # handle login command
    def login(self, name, password):
        self.username = name
        msg = "login " + name + " " + password
        self.socket.sendall(msg.encode())

    # handle list command
    def list(self):
        msg = "list"
        self.socket.sendall(msg.encode())

    # handle message command
    def message(self, username, msg):
        sent = "message " + self.username + " " + username + " " + msg + " MSGEND"
        self.socket.sendall(sent.encode())

    # handle logout command
    def logout(self):
        msg = "logout " + self.username
        self.socket.sendall(msg.encode())
        self.current_window.destroy()
        self.socket.close()

    # choose action for command buttons and return buttons by flag
    def choose_action(self, flag):
        if flag == "Register" or flag == "Login":
            self.input_layout(flag, "Username", "Password")  # go to input window
        elif flag == "List":
            self.list()  # send list command
        elif flag == "Message":
            self.input_layout(flag, "Username", "Message")  # go to input window
        elif flag == "Logout":
            self.logout()  # do logout
        elif flag == "Register_return" or flag == "Login_return":
            self.menu_layout("Register", "Login")  # show the menu window for register and login
        elif flag == "List_return" or flag == "Message_return":
            self.menu_layout("List", "Message", "Logout")  # show the menu window for list, message and logout

    # handle what will happen after click submit buttons
    def handle_submit(self, flag, ety1, ety2):
        # check the input boxes are not empty
        if ety1 == "" or ety2 == "":
            tk.messagebox.showinfo(title="error", message="input box can't be empty")
        else:
            if flag == "Register":
                self.register(ety1, ety2)
            elif flag == "Login":
                self.login(ety1, ety2)
            elif flag == "Message":
                self.message(ety1, ety2)

    # handle the received messages from server
    def handle_receive(self, words):
        for i in range(0, len(words)):
            word = words[i]
            # handle return message for register command
            if word == "Register":
                # rebuild the message and pop a message box
                reply = word + " " + words[i + 1]
                tk.messagebox.showinfo(title=word, message=reply)
                i += 2
            # handle return message for login command
            elif word == "Login":
                # rebuild the message
                reply = word + " " + words[i + 1]
                # success, go to after login menu
                if words[i + 1] == "success":
                    self.menu_layout("List", "Message", "Logout")
                # fail reset the username, pop a message box
                else:
                    self.username = None
                    tk.messagebox.showinfo(title=word, message=reply)
                i += 2
            # handle return message for list command
            elif word == "List":
                # rebuild message, each word (a username) in one line until find "LISTEND"
                lists = "Active users:\n"
                i += 1
                while words[i] != "LISTEND":
                    lists = lists + words[i] + "\n"
                    i += 1
                i += 1
                # open list window
                self.list_window(lists)
            # handle return message for message command
            elif word == "Message":
                # rebuild the message and pop a message box
                reply = word + " " + words[i + 1]
                tk.messagebox.showinfo(title=self.username + " " + word, message=reply)
                i += 2
            # handle the message sent by other clients
            elif word == "MSG":
                # rebuild the message and pop a message box
                msg = "You got a message from user " + words[i + 1] + "\nThe message is:"
                i += 2
                while words[i] != "MSGEND":
                    msg = msg + " " + words[i]
                    i += 1
                i += 1
                tk.messagebox.showinfo(title=self.username + " Received message", message=msg)

    # receive message from server
    def receive(self):
        inputs = [self.socket]
        if inputs:
            readable, writable, exceptional = select.select(inputs, inputs, inputs)
            for s in readable:
                if s is self.socket:
                    data = s.recv(1024).decode()
                    if data:
                        self.handle_receive(data.split())
            # set for timing operation
            self.after_id = self.current_window.after(100, self.receive)

    # run the gui
    def run(self):
        self.menu_layout("Register", "Login")


# run the client
if __name__ == '__main__':
    client = Client()
    client.run()
