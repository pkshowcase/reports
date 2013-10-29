package sp.model.ajax;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Complement model class for holding responses of Ajax requests. Convenient
 * holder for response results, target field or object, errors and status
 * messages.
 *
 * @author Paul Kulitski
 */
@XmlRootElement
public class AjaxResponse<T> implements Serializable {

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    /**
     * Status of the response. Mainly 'success' or 'error'. The presence of
     * {@link AjaxResponse#status} and {@link AjaxResponse#errors} depends on
     * that field
     */
    private String status;
    /**
     * The list of returned result object from the server. Generic. Mainly fills
     * up only when {@link AjaxResponse#SUCCESS}.
     */
    private List results;
    /**
     * The list of returned error from the server. Mainly fills up only if
     * {@link AjaxResponse#ERROR}.
     */
    private List<ErrorDetails> errors;

    public AjaxResponse() {
    }

    public AjaxResponse(String status) {
        this.status = status;
        /*
         * Need to  be instantiated for the successfull serialization by Jackson,
         * instead '500. Internal Server Error'  is throwed.
         */
        this.results = new ArrayList(0);
        this.errors = new ArrayList(0);
    }

    public AjaxResponse(String status, List results, List errors) {
        this.status = status;
        this.results = results;
        this.errors = errors;
    }

    public AjaxResponse(String status, Object result, ErrorDetails error) {
        this.status = status;
        this.results = new ArrayList();
        this.results.add(result);
        this.errors = new ArrayList();
        this.errors.add(error);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List getResults() {
        return results;
    }

    public void setResults(List results) {
        this.results = results;
    }

    public List getErrors() {
        return errors;
    }

    public void setErrors(List errors) {
        this.errors = errors;
    }

    public void addError(ErrorDetails error) {
        if (this.errors == null) {
            this.errors = new ArrayList();
            this.errors.add(error);
        } else {
            this.errors.add(error);
        }
    }

    public void addResult(Object result) {
        if (this.results == null) {
            this.results = new ArrayList();
            this.results.add(result);
        } else {
            this.results.add(result);
        }
    }

    public Object getSingleResult() {
        if (this.results.size() > 0) {
            return this.results.get(0);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "AjaxResponse{" + "status=" + status + ", results=" + results + ", errors=" + errors + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.status != null ? this.status.hashCode() : 0);
        hash = 73 * hash + (this.results != null ? this.results.hashCode() : 0);
        hash = 73 * hash + (this.errors != null ? this.errors.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AjaxResponse<T> other = (AjaxResponse<T>) obj;
        if ((this.status == null) ? (other.status != null) : !this.status.equals(other.status)) {
            return false;
        }
        if (this.results != other.results && (this.results == null || !this.results.equals(other.results))) {
            return false;
        }
        if (this.errors != other.errors && (this.errors == null || !this.errors.equals(other.errors))) {
            return false;
        }
        return true;
    }
}