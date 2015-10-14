cd /home/ubuntu/2IMN30-largelabexercise/;
git pull;
cd ~; 
ls > /home/ubuntu/tmp/`date +"%H:%M"`.txt
java -jar /home/ubuntu/2IMN30-largelabexercise/LargeLabExercise/dist/LargeLabExercise.jar "slave" > /home/ubuntu/logs/`date +"%m-%d-%y-%H:%M"`.txt

