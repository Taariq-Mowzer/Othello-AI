import os
import sys
import random
import time
sys.path.append(os.path.abspath("../"))

import Master


def random_move(board):
    #comment out sleep time if you don't want to watch
    time.sleep(0.2)

    
    tmp = board.find_all_moves()
    x = tmp[random.randint(0, len(tmp) - 1)]
    board.play_move(x)
    return str(x[0]) + ' ' + str(x[1])

if __name__ == "__main__":
    game = Master.Othello()
    s = input()
    if s == '-1':
        a = input().split()
        game.play_move((int(a[0]), int(a[1])))
    while True:
        a = random_move(game)
        print(a)
        a = input().split()
        game.play_move((int(a[0]), int(a[1])))
        
