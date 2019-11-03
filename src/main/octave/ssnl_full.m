function v = ssnl_full(A)
    n = size(A)(1);
    W = sum(A, 2);
    D = diag(W);
    v0 = (D .^ 0.5) * ones(n, 1);
    v0 = v0 / norm(v0);
    v = eye(n,n) + (D ^ -0.5) * A * (D ^ -0.5) - 2 * v0 * v0';
endfunction