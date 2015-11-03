
len = 6850;
a = load('best_theta.txt');
org_len = size(a,1)
total_time = 85507.88;
a = a(1:len);
x = 1:len;
x = (x/org_len*total_time)';

scatter(x, a);
set(gca,'FontSize', 18);

size(a)
size(x)

fitlm(x, a)

% hold on 
% yCalc2 = x*b;
% plot(x,yCalc2,'--')

xlabel('Elapsed Time (s)', 'FontSize', 18);
ylabel('Offset (s)', 'FontSize', 18);

axis([0 70000 0 2.5])
grid