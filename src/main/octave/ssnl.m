function v = ssnl(A)
    n = size(A)(1);
    L = normalized_laplacian(A);
    [u,v] = eigs(L, n);
    v0 = u(:,n);
    n = size(A)(1);
    v = 2 * (eye(n,n) - v0 * v0') - L;
endfunction