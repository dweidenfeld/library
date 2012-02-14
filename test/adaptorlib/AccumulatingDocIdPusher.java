// Copyright 2011 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package adaptorlib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class AccumulatingDocIdPusher extends AbstractDocIdPusher {
  private List<DocId> ids = new ArrayList<DocId>();

  @Override
  public Record pushRecords(Iterable<Record> records,
                          PushErrorHandler handler)
      throws InterruptedException {
    for (Record record : records) {
      ids.add(record.getDocId());
    }
    return null;
  }

  public List<DocId> getDocIds() {
    return Collections.unmodifiableList(ids);
  }

  public void reset() {
    ids.clear();
  }

  @Override
  public DocId pushNamedResources(Map<DocId, Acl> resources,
                                  PushErrorHandler hanlder)
      throws InterruptedException {
    throw new UnsupportedOperationException();
  }
}
