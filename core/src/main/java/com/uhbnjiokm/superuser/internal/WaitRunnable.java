/*
 * Copyright 2019 John "uhbnjiokm" Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uhbnjiokm.superuser.internal;

public final class WaitRunnable implements Runnable {

    private Runnable r;
    private boolean done = false;

    public WaitRunnable(Runnable run) {
        r = run;
    }

    public synchronized void waitUntilDone() {
        while (!done) {
            try {
                wait();
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public synchronized void run() {
        r.run();
        done = true;
        notifyAll();
    }
}
