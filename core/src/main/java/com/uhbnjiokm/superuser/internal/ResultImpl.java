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

import androidx.annotation.NonNull;

import com.uhbnjiokm.superuser.Shell;

import java.util.Collections;
import java.util.List;

class ResultImpl extends Shell.Result {
    List<String> out;
    List<String> err;
    int code = JOB_NOT_EXECUTED;
    static Shell.Result INSTANCE = new ResultImpl();
    static Shell.Result SHELL_ERR = new ResultImpl();

    @NonNull
    @Override
    public List<String> getOut() {
        return out == null ? Collections.emptyList() : out;
    }

    @NonNull
    @Override
    public List<String> getErr() {
        return err == null ? Collections.emptyList() : err;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public boolean isSuccess() {
        return code == 0;
    }
}
