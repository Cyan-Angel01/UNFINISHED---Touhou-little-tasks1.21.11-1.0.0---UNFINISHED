import time

#####Color codes#####
Green = "\033[32m"
Cyan = "\033[36m"
Red = "\033[31m"
zero = "\033[0m"
Blue = "\033[34m"
Magenta = "\033[35m"
Yellow = "\033[33m"
Purple = "\033[95m"
Orange = "\033[38;5;208m"
Pink = "\033[38;5;205m"

#####another about colors#####
import builtins

def print(*args, **kwargs):
    if args:
        args = list(args)
        args[-1] = str(args[-1]) + "\033[0m"
    builtins.print(*args, **kwargs)

def input(prompt=""):
    return builtins.input(str(prompt) + "\033[0m")
# -----------------------------------------------------



#Main op
print(Cyan + "Hai! Please input your name to start!" + zero)
time.sleep(1)
name = input(">>   " )

if len(name) > 0
    partial = name.split()
    
    partial[0] = 1
    partial[1] = 2
    print(f" " + Cyan + "Hai, {1}! Is that okay?")
    time.sleep(0.5)
    input("Yeah, thought so." + zero "(press enter to continue)")
    input("Okay. Now i'll tell you... This actuall^ isn't anything!")
    input("out of boredom, I posted this along. No reason why. Just wanted to.")
    input(f"Now go. Shoo. Scram, {1}. May we meet again.")
    input("Oh, you're still here?")
    input("Okay. I'm Ka1t0. I will appear as a python file in every posts of Cyan-Angel01")
    input("Yes. You will find me in Cyan-Angel01's posts." + Red + "...Unless he forgets to write me")
    input(Cyan + "Everytime, my script will be different. And, the purpose of this script is to slowly reveal the real Cyan-Angel01")
    input(Yellow + "So... yeah. Let me give you a hint before you go...")
    input(zero + "His real name starts with K. Not Kaito. That's his anonymous persona.")
    input(Cyan + "So... Yeah. That's all for now. Bye.")
    input(zero + "(press enter to end)")

else:
    print("Error, no name typed. Please try again.")
