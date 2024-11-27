#/bin/bash

#RMF
# python3 plots.py data/RMF_UNIFORM_RMSE data/RMF_GAUSSIAN_RMSE --xlabel  "epoki" --ylabel "RMSE" --title "RMF_RMSE"           
# python3 plots.py data/RMF_UNIFORM_MAE data/RMF_GAUSSIAN_MAE --xlabel  "epoki" --ylabel "MAE" --title "RMF_MAE"           

#MMMF
# python3 plots.py data/MMMF_UNIFORM_MAE data/MMMF_GAUSSIAN_MAE --xlabel  "epoki" --ylabel "MAE" --title "MMMF_MAE" 
# python3 plots.py data/MMMF_UNIFORM_RMSE data/MMMF_GAUSSIAN_RMSE --xlabel  "epoki" --ylabel "RMSE" --title "MMMF_RMSE"

#NMF
# python3 plots.py data/NMF_DIVERGENCE_MAE data/NMF_EUCLIDEAN_MAE --xlabel  "epoki" --ylabel "MAE" --title "NMF_MAE"
# python3 plots.py data/NMF_DIVERGENCE_RMSE data/NMF_EUCLIDEAN_RMSE --xlabel  "epoki" --ylabel "RMSE" --title "NMF_RMSE"

#RMF vs MMMF
# python3 plots.py data/MMMF_UNIFORM_RMSE data/RMF_UNIFORM_RMSE --xlabel  "epoki" --ylabel "RMSE" --title "UNIFORM_RMSE"           
# python3 plots.py data/MMMF_UNIFORM_MAE data/RMF_UNIFORM_MAE --xlabel  "epoki" --ylabel "MAE" --title "UNIFORM_MAE"   

#RMF K
# python3 plots.py data/RMF_UNIFORM_RMSE data/RMF_50_RMSE data/RMF_100_RMSE --xlabel  "epoki" --ylabel "RMSE" --title "RMF_K_RMSE"           
# python3 plots.py data/RMF_UNIFORM_MAE data/RMF_50_MAE data/RMF_100_MAE --xlabel  "epoki" --ylabel "MAE" --title "RMF_K_MAE"  

#RMF SQRT vs K
# python3 plots.py data/RMF_100_RMSE data/RMF_sqrt100_RMSE --xlabel  "epoki" --ylabel "RMSE" --title "RMF_100_RMSE"           
# python3 plots.py data/RMF_100_MAE data/RMF_sqrt100_MAE --xlabel  "epoki" --ylabel "MAE" --title "RMF_100_MAE"

#RMF RMF VS GA
# python3 plots.py data/RMF_UNIFORM_RMSE data/GA_OPTYMALNY_RMSE --xlabel  "epoki" --ylabel "RMSE" --title "GA_RMSE"           
# python3 plots.py data/RMF_UNIFORM_MAE data/GA_OPTYMALNY_MAE --xlabel  "epoki" --ylabel "MAE" --title "GA_MAE"   

#RMF RMF VS SWARM
# python3 plots.py data/RMF_UNIFORM_RMSE data/SWARM_OPTYMALNY_RMSE --xlabel  "epoki" --ylabel "RMSE" --title "PSO_RMSE"           
# python3 plots.py data/RMF_UNIFORM_MAE data/SWARM_OPTYMALNY_MAE --xlabel  "epoki" --ylabel "MAE" --title "PSO_MAE"   

#GA VS SWARM
#python3 plots.py data/GA_OPTYMALNY_RMSE data/SWARM_OPTYMALNY_RMSE --xlabel  "epoki" --ylabel "RMSE" --title "GA_PSO_RMSE"   
#python3 plots.py data/GA_OPTYMALNY_MAE data/SWARM_OPTYMALNY_MAE --xlabel  "epoki" --ylabel "MAE" --title "GA_PSO_MAE"

#KNN USER
python3 plots.py data/knn_user_pearson_RMSE data/knn_user_euclidean_RMSE data/knn_user_adjustedCosine_RMSE --xlabel  "k" --ylabel "RMSE" --title "user_based_RMSE"   
python3 plots.py data/knn_user_pearson_MAE data/knn_user_euclidean_MAE data/knn_user_adjustedCosine_MAE --xlabel  "k" --ylabel "MAE" --title "user_based_MAE"

#KNN ITEM
python3 plots.py data/knn_item_pearson_RMSE data/knn_item_euclidean_RMSE data/knn_item_adjustedCosine_RMSE --xlabel  "k" --ylabel "RMSE" --title "item_based_RMSE"   
python3 plots.py data/knn_item_pearson_MAE data/knn_item_euclidean_MAE data/knn_item_adjustedCosine_MAE --xlabel  "k" --ylabel "MAE" --title "item_based_MAE"