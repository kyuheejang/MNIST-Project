import os
import tensorflow as tf
import numpy as np
import scipy
import base64
import json
from PIL import Image
from flask import Flask, request,jsonify
from tensorflow.examples.tutorials.mnist import  input_data

models = []
class Model:
    def __init__(self, sess, name):
        self.sess = sess
        self.name = name
        self._build_net()

    def _build_net(self):
        with tf.variable_scope(self.name):
            self.training = tf.placeholder(tf.bool)
            self.X = tf.placeholder(tf.float32, [None, 784])
            X_img = tf.reshape(self.X, [-1, 28, 28, 1])
            self.Y = tf.placeholder(tf.float32, [None,10])

            #conv1
            conv1 = tf.layers.conv2d(inputs= X_img, filters=32, kernel_size=[3,3], padding="SAME", activation=tf.nn.relu)
            pool1 =  tf.layers.max_pooling2d(inputs= conv1, pool_size=[2,2], padding="SAME", strides=2)
            dropout1 = tf.layers.dropout(inputs=pool1, rate= 0.7, training=self.training)

            #conv2
            conv2 = tf.layers.conv2d(inputs=dropout1, filters=64, kernel_size=[3, 3], padding="SAME", activation=tf.nn.relu)
            pool2 = tf.layers.max_pooling2d(inputs=conv2, pool_size=[2, 2], padding="SAME", strides=2)
            dropout2 = tf.layers.dropout(inputs=pool2, rate=0.7, training=self.training)

            #conv3
            conv3 = tf.layers.conv2d(inputs=dropout2, filters=128, kernel_size=[3, 3], padding="SAME", activation=tf.nn.relu)
            pool3 = tf.layers.max_pooling2d(inputs=conv3, pool_size=[2, 2], padding="SAME", strides=2)
            dropout3 = tf.layers.dropout(inputs=pool3, rate=0.7, training=self.training)

            #Dense Layer with Relu
            flat = tf.reshape(dropout3, [-1, 128*4*4])
            dense4 = tf.layers.dense(inputs=flat, units=625, activation=tf.nn.relu)
            dropout4 = tf.layers.dropout(inputs=dense4, rate= 0.5, training= self.training)

            # Logits (no activation) Layer: L5 Final FC 625 inputs -> 10 outputs
            self.logits = tf.layers.dense(inputs=dropout4, units=10)

            # define cost/loss & optimizer
        self.cost = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(
                logits=self.logits, labels=self.Y))
        self.optimizer = tf.train.AdamOptimizer(
                learning_rate=learning_rate).minimize(self.cost)

    def train(self, x_data, y_data, training = False):
        return self.sess.run([self.cost, self.optimizer], feed_dict = {self.X:x_data, self.Y: y_data, self.training: training})

    def predict(self, x_test, training=False):
        return self.sess.run(self.logits, feed_dict={self.X: x_test, self.training: training})

app= Flask(__name__)
tf.set_random_seed(777)
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'
batch_size = 100
training_epochs = 10
learning_rate = 0.001

@app.route('/train')
def train():
    mnist = input_data.read_data_sets("MNIST_data/", one_hot=True)
    num_models = 3
    sess = tf.Session()
    for m in range(num_models):
        models.append(Model(sess, "model" + str(m)))
    sess.run(tf.global_variables_initializer())

    print('Learning Started!')
    for epoch in range(training_epochs):
        avg_costs_list = np.zeros(len(models))
        total_batch = int(mnist.train.num_examples / batch_size)
        for i in range(total_batch):
            batch_xs, batch_ys = mnist.train.next_batch(batch_size)
            for m_idx, m in enumerate(models):
                c, _ = m.train(batch_xs, batch_ys)
                avg_costs_list[m_idx] += c / total_batch
        print('Epoch:', '%04d' % (epoch + 1), 'cost =', '{:.9f}', avg_costs_list)
    print('Learning Finished!')
    return 'Learning Finish'

@app.route('/test', methods = ['POST'])
def test():
    if request.method == 'POST':
        Get_Data=request.get_data()
        Byte_to_String = Get_Data.decode('utf-8')
        Jsondata=json.loads(Byte_to_String,strict=False)
        picture = Jsondata['picture']
        imagedata = base64.b64decode(picture)
        filename = 'practice.png'
        with open(filename, 'wb') as f:
            f.write(imagedata)

        foo = Image.open('practice.png').convert('L')
        foo = foo.resize((28, 28), Image.ANTIALIAS)
        foo.save("practice2.png")
        im = np.vectorize(lambda x: 255 - x)(np.ndarray.flatten(scipy.ndimage.imread("practice2.png", flatten=True)))
        im = np.reshape(im,(1,784))

        sess = tf.Session()
        test_size = 1
        predictions = np.zeros(10).reshape(test_size, 10)

        for m_idx, m in enumerate(models):
            p = m.predict(im)
            predictions += p
        print(sess.run(tf.argmax(predictions, 1)))
        Value = sess.run(tf.argmax(predictions, 1))
        Value = Value.item(0)
        message = {"value" : str(Value) }
        return jsonify(message)
    else:
        print('error')

if __name__ == '__main__':
    app.debug = True
    app.run(host='YOUR URL', port = 9991)



