# About Dataset
This project uses three main datasets, namely random-image-sample-dataset, blur-dataset, and rice-leaf-disease-image. The random-image-sample-dataset and blur-dataset are placed in the Dataset/non_paddy directory, while the rice-leaf-disease-image dataset is located in the Dataset/Paddy directory. The structure of these datasets is systematically designed to ensure that each class is well-organized, thus facilitating the process of model training and performance evaluation effectively.

# How To Use The Notebook
1. Open Google Colab. Go to https://colab.research.google.com/ and sign in with your Google account if necessary.
2. Create a New Notebook. Click on "File" in the top-left corner of the Colab interface and select "New Notebook" to create a new notebook.
3. You can clone this repo or just download the notebook.
4. Open the notebook in Google Colab.
5. Follow the instructions in the notebook and run the notebook.

# How to test Flask API using cmd
1. Download the Flask API folder (in master branch).
2. Download and copy model h5 to Flask directory.
3. Open cmd and navigate to Flask API directory.
4. Create a Python virtual environment by running this following command:
   
   ```pip install virtualenv```

    then run this:

    ```virtualenv ripad```

   Note: ripad is my virtual environment name
   
6. In file explorer, you can see virtual environment created successfully.

7. Activate virtual environment by running this following command:

   ```ripad\Scripts\activate.bat```
8. Left side we can see (skinsight) before file directory, that means virtual environment is active. Now virtual environment is activated, we will install required library for the project.

9. Install the required library by running this following command:

   ```pip install flask```

   ```pip install tensorflow```

   ```pip install pillow```

   Note: make sure you have a compatible version of Python installed on your system as well.

10. Run the flask application

   ```python main.py```
11. Server is running. Open url [http://127.0.0.1:5000/](http://127.0.0.1:5000/) in browser.
   
