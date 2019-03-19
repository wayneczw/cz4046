import matplotlib.pyplot as plt
import collections

file_path = 'valueiteration/valueiteration_hist.txt'
fig_path = 'valueiteration/utilities_against_iterations.png'
algo = 'Value Iteration'

# file_path = 'policyiteration/policyiteration_hist.txt'
# fig_path = 'policyiteration/utilities_against_iterations.png'
# algo = 'Policy Iteration'

# file_path = 'complexpolicyiteration/policyiteration_hist.txt'
# fig_path = 'complexpolicyiteration/utilities_against_iterations.png'
# algo = 'Policy Iteration'

# file_path = 'complexvalueiteration/valueiteration_hist.txt'
# fig_path = 'complexvalueiteration/utilities_against_iterations.png'
# algo = 'Value Iteration'

with open(file_path, 'r') as f:
    f_list = f.readlines()

f_list = [i.strip() for i in f_list]

data_dict = collections.OrderedDict()
ROW = 6
COL = 6

for r in range(ROW):
    for c in range(COL):
        coord = '({}, {})'.format(r, c)
        data_dict[coord] = list() 

for line in f_list:
    if line.startswith('=='):
        r = 0
        c = 0
        continue
    coord = '({}, {})'.format(r, c)
    data_dict[coord].append(float(line))

    if c < (COL - 1):
        c += 1
    else:
        c = 0
        r += 1

plt.figure(algo)
plt.title("{} - Utilities against Iterations".format(algo))
for k, v in data_dict.items():
    if sum(v) == 0:
        continue
    plt.plot(range(len(v)), v, label=k)
    plt.xlabel('Iterations')
    plt.ylabel('Utilities')
    plt.legend(loc='center left', bbox_to_anchor=(1, 0))
    plt.grid(b=True)

plt.savefig(fig_path, bbox_inches='tight')
