
mean_value = -24.0;
range = 40;
max_value = range + mean_value;
min_value = -range + mean_value;

a = load('cdr.txt');

% Y = prctile(a,97)

a = a(a<max_value);
a = a(a>min_value);

mean(a)
% mean(abs(a))

hist(a,100);
set(gca,'FontSize', 18);
axis([min_value max_value 0 800]);
xlabel('Average Clock Drift Rate (10^{-6}s/s)');
ylabel('Count');

grid on

% % GMModel = fitgmdist(a,3,'RegularizationValue', 1e-5)
