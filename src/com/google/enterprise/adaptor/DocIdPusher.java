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

package com.google.enterprise.adaptor;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Interface that allows at-will pushing of {@code DocId}s to the GSA.
 */
public interface DocIdPusher {
  /**
   * Push {@code DocId}s immediately and block until they are successfully
   * provided to the GSA or the error handler gives up. This method can take a
   * while in error conditions, but is not something that generally needs to be
   * avoided.
   *
   * <p>Equivalent to {@code pushDocIds(docIds, null)} and {@link
   * #pushRecords(Iterable)} with default values for each {@code Record}.
   *
   * @return {@code null} on success, otherwise the first DocId to fail
   * @throws InterruptedException if interrupted and no DocIds were sent
   * @see #pushDocIds(Iterable, ExceptionHandler)
   */
  public DocId pushDocIds(Iterable<DocId> docIds)
      throws InterruptedException;

  /**
   * Push {@code DocId}s immediately and block until they are successfully
   * provided to the GSA or the error handler gives up. This method can take a
   * while in error conditions, but is not something that generally needs to be
   * avoided.
   *
   * <p>If handler is {@code null}, then a default error handler is used.
   *
   * <p>Equivalent to {@link #pushRecords(Iterable, ExceptionHandler)}
   * with default values for each {@code Record}.
   *
   * @return {@code null} on success, otherwise the first DocId to fail
   * @throws InterruptedException if interrupted and no DocIds were sent
   */
  public DocId pushDocIds(Iterable<DocId> docIds, ExceptionHandler handler)
      throws InterruptedException;

  /**
   * Push {@code Record}s immediately and block until they are successfully
   * provided to the GSA or the error handler gives up. This method can take a
   * while in error conditions, but is not something that generally needs to be
   * avoided.
   *
   * <p>Equivalent to {@code pushRecords(records, null)}.
   *
   * @return {@code null} on success, otherwise the first Record to fail
   * @throws InterruptedException if interrupted and no Records were sent
   * @see #pushRecords(Iterable, ExceptionHandler)
   */
  public Record pushRecords(Iterable<Record> records)
      throws InterruptedException;

  /**
   * Push {@code Record}s immediately and block until they are successfully
   * provided to the GSA or the error handler gives up. This method can take a
   * while in error conditions, but is not something that generally needs to be
   * avoided.
   *
   * <p>If handler is {@code null}, then a default error handler is used.
   *
   * @return {@code null} on success, otherwise the first Record to fail
   * @throws InterruptedException if interrupted and no Records were sent
   */
  public Record pushRecords(Iterable<Record> records, ExceptionHandler handler)
      throws InterruptedException;

  /**
   * Push named resources immediately and block until they are successfully
   * provided to the GSA or the error handler gives up. This method can take a
   * while in error conditions, but is not something that generally needs to be
   * avoided.
   *
   * <p>Named resources are {@code DocId}s without any content or metadata, that
   * only exist for ACL inheritance. These {@code DocId} will never be visible
   * to the user and have no meaning outside of ACL processing.
   *
   * <p>If you plan on using the return code, then the provided map should have
   * a predictable iteration order, like {@link java.util.TreeMap}.
   *
   * <p>Equivalent to {@code pushNamedResources(resources, null)}.
   *
   * @return {@code null} on success, otherwise the first DocId to fail
   * @throws InterruptedException if interrupted and no resources were sent
   * @see #pushNamedResources(Map, ExceptionHandler)
   */
  public DocId pushNamedResources(Map<DocId, Acl> resources)
      throws InterruptedException;

  /**
   * Push named resources immediately and block until they are successfully
   * provided to the GSA or the error handler gives up. This method can take a
   * while in error conditions, but is not something that generally needs to be
   * avoided.
   *
   * <p>Named resources are {@code DocId}s without any content or metadata, that
   * only exist for ACL inheritance. These {@code DocId} will never be visible
   * to the user and have no meaning outside of ACL processing.
   *
   * <p>If you plan on using the return code, then the provided map should have
   * a predictable iteration order, like {@link java.util.TreeMap}.
   *
   * <p>If handler is {@code null}, then a default error handler is used.
   *
   * @return {@code null} on success, otherwise the first DocId to fail
   * @throws InterruptedException if interrupted and no resources were sent
   */
  public DocId pushNamedResources(Map<DocId, Acl> resources,
                                  ExceptionHandler handler)
      throws InterruptedException;

  /**
   * Immutable feed attributes for a document identified by its {@code DocId}.
   */
  public static final class Record implements DocIdSender.Item {
    private final DocId id;
    private final boolean delete;
    private final Date lastModified;
    private final URI link;
    private final boolean crawlImmediately;
    private final boolean crawlOnce;
    private final boolean lock;

    private Record(DocId docid, boolean delete, Date lastModified,
        URI link, boolean crawlImmediately, boolean crawlOnce, boolean lock) {
      this.id = docid;
      this.delete = delete;
      this.lastModified = lastModified;
      this.link = link;
      this.crawlImmediately = crawlImmediately;
      this.crawlOnce = crawlOnce;
      this.lock = lock;
    }

    /**
     * The identifier for the document this record is providing information for.
     *
     * @return non-{@code null} identifier for the document
     */
    public DocId getDocId() {
      return id;
    }

    /**
     * Whether the GSA is being informed the document has been deleted.
     */
    public boolean isToBeDeleted() {
      return delete;
    }

    /**
     * The last modified date of the document. This is used for determining that
     * the GSA's version is older and that the GSA should recrawl soon (instead
     * of natually discovering the modification). If {@code null}, then natural
     * crawling is the primary method of detecting modifications.
     */
    public Date getLastModified() {
      return lastModified;
    }

    /**
     * The URI that should be displayed to the user in search results. If {@code
     * null}, then the crawl URI representing the {@code DocId} is used.
     */
    public URI getResultLink() {
      return link;
    }

    /**
     * Informs the GSA that the document has been modified, and the GSA should
     * give high priority to recrawling the document.
     */
    public boolean isToBeCrawledImmediately() {
      return crawlImmediately;
    }

    /**
     * Informs the GSA that it should only crawl the document once. This
     * disables automatic detection of modifications by the GSA for this
     * document.
     */
    public boolean isToBeCrawledOnce() {
      return crawlOnce;
    }

    /**
     * Locks the document into the GSA's index. This informs the GSA that it
     * should choose to evict other documents from its index when the document
     * license limit is reached.
     */
    public boolean isToBeLocked() {
      return lock;
    }

    /**
     * Checks for equality based on all visible fields.
     */
    @Override
    public boolean equals(Object o) {
      boolean same = false;
      if (null != o && this.getClass().equals(o.getClass())) {
        Record other = (Record) o;
        same = this.id.equals(other.id)
            && (this.delete == other.delete)
            && (this.crawlImmediately == other.crawlImmediately)
            && (this.crawlOnce == other.crawlOnce)
            && (this.lock == other.lock)
            && equalsNullSafe(lastModified, other.lastModified)
            && equalsNullSafe(link, other.link);
      }
      return same;
    }

    /**
     * Generates hash code based on all visible fields.
     */
    @Override
    public int hashCode() {
      Object members[] = new Object[] { id, delete, lastModified, link,
          crawlImmediately, crawlOnce, lock };
      return Arrays.hashCode(members);
    }

    /**
     * Generates a string representation of this instance useful for debugging
     * that contains all visible fields.
     */
    @Override
    public String toString() {
      return "Record(docid=" + id.getUniqueId()
          + ",delete=" + delete
          + ",lastModified=" + lastModified
          + ",resultLink=" + link
          + ",crawlImmediately=" + crawlImmediately
          + ",crawlOnce=" + crawlOnce
          + ",lock=" + lock + ")";
    }

    private static boolean equalsNullSafe(Object a, Object b) {
      boolean same;
      if (null == a && null == b) {
        same = true;
      } else if (null != a && null != b) {
        same = a.equals(b);
      } else {
        same = false;
      }
      return same;
    }

    /**
     * Builder to create instances of Record.
     */
    public static class Builder {
      private DocId docid = null;
      private boolean delete = false;
      private Date lastModified = null;
      private URI link = null;
      private boolean crawlImmediately = false;
      private boolean crawlOnce = false;
      private boolean lock = false;

      /**
       * Create mutable builder for building {@link Record} instances.
       *
       * @param id non-{@code null} identifier for the document being described
       */
      public Builder(DocId id) {
        if (null == id) {
          throw new NullPointerException();
        }
        docid = id;
      }

      /**
       * Create mutable builder initialized to values provided by {@code
       * startPoint}. This is useful for making changes to an existing {@code
       * Record}.
       */
      public Builder(Record startPoint) {
        this.docid = startPoint.id;
        this.delete = startPoint.delete;
        this.lastModified = startPoint.lastModified;
        this.link = startPoint.link;
        this.crawlImmediately = startPoint.crawlImmediately;
        this.crawlOnce = startPoint.crawlOnce;
        this.lock = startPoint.lock;
      }

      /**
       * Set the identifier for the document this record is providing
       * information for. This replaces the value provided to the constructor.
       *
       * @param id non-{@code null} identifier for the document
       * @return the same instance of the builder, for chaining calls
       */
      public Builder setDocId(DocId id) {
        if (null == id) {
          throw new NullPointerException();
        }
        this.docid = id;
        return this;
      }

      /**
       * Set whether the GSA is being informed the document has been deleted.
       * When {@code false}, the GSA is being informed the document exists. The
       * default is {@code false}.
       *
       * @return the same instance of the builder, for chaining calls
       */
      public Builder setDeleteFromIndex(boolean b) {
        this.delete = b;
        return this;
      }

      /**
       * Provides the last-modified date of the document. This is used by the
       * GSA to learn that the document has been modified since the GSA last
       * retrieved the document's contents. When {@code null}, the GSA must use
       * its natural crawling of content to discover changes. The default is
       * {@code null}.
       *
       * @return the same instance of the builder, for chaining calls
       */
      public Builder setLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
      }

      /**
       * Set the URI to be displayed to the user in search results. If {@code
       * null}, then the crawl URI representing the {@code DocId} is used. The
       * default is {@code null}.
       *
       * @return the same instance of the builder, for chaining calls
       */
      public Builder setResultLink(URI link) {
        this.link = link;
        return this;
      }

      /**
       * Inform the GSA that the document has been modified, and that the GSA
       * should give high priority to recrawling the document. The default is
       * {@code false}.
       *
       * @return the same instance of the builder, for chaining calls
       */
      public Builder setCrawlImmediately(boolean crawlImmediately) {
        this.crawlImmediately = crawlImmediately;
        return this;
      }

      /**
       * Instruct the GSA to not recrawl the document after the initial
       * retrieval. The default is {@code false}.
       *
       * @return the same instance of the builder, for chaining calls
       */
      public Builder setCrawlOnce(boolean crawlOnce) {
        this.crawlOnce = crawlOnce;
        return this;
      }

      /**
       * Instruct the GSA to "lock" the document into its index. This causes
       * other documents to be evicted from the index when the document license
       * limit is reached. The default is {@code false}.
       *
       * @return the same instance of the builder, for chaining calls
       */
      public Builder setLock(boolean lock) {
        this.lock = lock;
        return this;
      }

      /** Creates single instance of Record.  Does not reset builder. */
      public Record build() {
        return new Record(docid, delete, lastModified,
            link, crawlImmediately, crawlOnce, lock);
      }
    }
  }
}
