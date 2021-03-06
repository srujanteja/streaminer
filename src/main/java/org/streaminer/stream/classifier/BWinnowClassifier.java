/**
 * Copyright 2013-2015 Pierre Merienne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streaminer.stream.classifier;

import org.streaminer.util.math.MathUtil;

/**
 * Balanced Winnow Classifier
 * 
 * @see http://www.cs.cmu.edu/~vitor/papers/kdd06_final.pdf
 * @author pmerienne
 * 
 */
public class BWinnowClassifier extends SimpleClassifier<Boolean> {
    private static final long serialVersionUID = -5163481593640555140L;

    /**
     * Positive model
     */
    private double[] u;

    /**
     * Negative model
     */
    private double[] v;

    private double promotion = 1.5;
    private double demotion = 0.5;
    private double threshold = 1.0;

    public BWinnowClassifier() {
    }

    public BWinnowClassifier(double promotion, double demotion, double threshold) {
        this.promotion = promotion;
        this.demotion = demotion;
        this.threshold = threshold;
    }

    @Override
    public Boolean predict(double[] features) {
        if (this.u == null || this.v == null) {
            init(features.length);
        }

        Double evaluation = MathUtil.dot(features, this.u) - MathUtil.dot(features, this.v) - this.threshold;

        Boolean prediction = evaluation >= 0 ? Boolean.TRUE : Boolean.FALSE;
        return prediction;
    }

    @Override
    public void learn(Boolean label, double[] features) {
        Boolean predictedLabel = predict(features);

        // The model is updated only when a mistake is made
        if (!label.equals(predictedLabel)) {
            for (int i = 0; i < features.length; i++) {
                if (features[i] > 0) {
                    if (predictedLabel) {
                        // Demotion step
                        this.u[i] = this.u[i] * this.demotion;
                        this.v[i] = this.v[i] * this.promotion;
                    } else {
                        // Promotion step
                        this.u[i] = this.u[i] * this.promotion;
                        this.v[i] = this.v[i] * this.demotion;
                    }
                }
            }
        }
    }

    protected void init(int featureSize) {
        // Init models
        this.u = new double[featureSize];
        this.v = new double[featureSize];

        for (int i = 0; i < featureSize; i++) {
            this.u[i] = 2 * this.threshold / featureSize;
            this.v[i] = this.threshold / featureSize;
        }
    }

    public void reset() {
        this.u = null;
        this.v = null;
    }

    public double[] getU() {
        return u;
    }

    public void setU(double[] u) {
        this.u = u;
    }

    public double[] getV() {
        return v;
    }

    public void setV(double[] v) {
        this.v = v;
    }

    public double getPromotion() {
        return promotion;
    }

    public void setPromotion(double promotion) {
        this.promotion = promotion;
    }

    public double getDemotion() {
        return demotion;
    }

    public void setDemotion(double demotion) {
        this.demotion = demotion;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "BWinnowClassifier [promotion=" + promotion + ", demotion=" + demotion + ", threshold=" + threshold + "]";
    }

}