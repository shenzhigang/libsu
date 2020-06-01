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

import com.uhbnjiokm.superuser.ShellUtils;
import com.uhbnjiokm.superuser.io.SuFile;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * An implementation specialized for block devices
 */
class ShellBlockIO extends ShellIO {

    // Block size is constant
    private long blockSize;

    ShellBlockIO(SuFile file, String mode) throws FileNotFoundException {
        super(file, mode);
        if (Env.blockdev()) {
            try {
                blockSize = Long.parseLong(ShellUtils.fastCmd(
                        "blockdev --getsize64 '" + file.getAbsolutePath() + "'"));
            } catch (NumberFormatException e) {
                blockSize = Long.MAX_VALUE;
            }
        } else {
            // No blockdev available, no choice but to assume no boundary
            blockSize = Long.MAX_VALUE;
        }
        WRITE_CONV = "";
    }

    @Override
    int read(byte[] b, int off, int len, long fileOff, long bs) throws IOException {
        // dd skip past boundary is extremely slow, avoid it
        if (fileOff >= blockSize) {
            eof = true;
            return -1;
        }
        return super.read(b, off, len, fileOff, bs);
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        if (fileOff + len > blockSize)
            throw new IOException("Cannot write pass block size");
        super.write(b, off, len);
    }

    @Override
    void streamWrite(byte[] b, int off, int len) throws IOException {
        // Block devices cannot use append
        write(b, off, len);
    }

    @Override
    public long length() {
        return blockSize;
    }

    @Override
    public void setLength(long newLength) {
        throw new UnsupportedOperationException("Block devices have fixed sizes");
    }

    @Override
    public void seek(long pos) throws IOException {
        if (pos > blockSize)
            throw new IOException("Cannot seek pass block size");
        fileOff = pos;
    }
}
